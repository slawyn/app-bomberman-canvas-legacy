package main.Essentials;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.DataOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

import main.Globals;

import android.util.Log;

import java.util.UUID;

import main.Constants;

// TODO, improve throuput and reduce latency
// https://github.com/greatscottgadgets/ubertooth/wiki/One-minute-to-understand-BLE-MTU-data-package
// https://punchthrough.com/maximizing-ble-throughput-part-2-use-larger-att-mtu-2/
public class Client extends Pipe {
    private final String LOGTAG = "Client";
    private boolean clientrunning;
    private BluetoothSocket serversocket;
    private DataOutputStream os;
    private DataInputStream is;
    private byte buffer[][];

    public Client(BluetoothDevice serverdevice, Pipe pipe) {
        UUID uuid = UUID.fromString(Constants.SERVER_UUID);
        try {

            Log.e(LOGTAG, "Services: " + serverdevice.fetchUuidsWithSdp());
            serversocket = serverdevice.createRfcommSocketToServiceRecord(uuid);
            serversocket.connect();

            buffer = new byte[Constants.NUM_OF_PACKET_DESCS][Constants.MAX_PACKET_SIZE];

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Log.e(LOGTAG, "Connected...");
        try {

            // Connect Pipes to App
            pipeIn = pipe.getPipeIn();
            pipeOut = pipe.getPipeOut();

            // Bluetooth Streams
            os = new DataOutputStream(serversocket.getOutputStream());
            is = new DataInputStream(serversocket.getInputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }

        clientrunning = true;

        /* Start threads*/
        new Thread(incomingTraffic, "##Client Reading Thread").start();
        Log.e(LOGTAG, "Started threads...");
    }

    public void closeConnnection() {
        try {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            clientrunning = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Runnable incomingTraffic = new Runnable() {

        @Override
        public void run() {
            try {

                int idx = 0;
                byte b[] = new byte[5];

                // Send back a reply
                byte c[] = new byte[]{Constants.PROTOCOL_ID_DISTRIBUTION, Globals.clientID,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

                // ID Synchronization
                while(true){
                    if(is.read(b, 0, 5) != -1){
                        if(b[1] == Constants.PROTOCOL_ID_DISTRIBUTION) {
                            Globals.clientID = b[0];
                            Globals.numberOfPlayers = b[2];

                            os.write(b);
                            os.flush();

                            break;
                        }
                    }
                    Thread.sleep(1);
                }

                Log.e(LOGTAG, "This connection has been approved");
                new Thread(outgoingTraffic, "##Client Writing Thread").start();

                // TODO optimize
                while (clientrunning) {
                    if (is.read(buffer[idx], 0, Constants.MAX_PACKET_SIZE) != -1) {
                        Globals.numberOfReceivedPackets++;
                        pipeIn.add(buffer[idx]);
                        idx = (idx+1)%Constants.NUM_OF_PACKET_DESCS;
                    } else {
                        Thread.sleep(1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable outgoingTraffic = new Runnable() {
        @Override
        public void run() {
            try {

                /* start Listening*/
                while (clientrunning) {
                    if (!pipeOut.isEmpty()) {
                        os.write(pipeOut.remove());
                        os.flush();
                        Globals.numberOfSentPackets++;
                    }
                    Thread.sleep(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

}



