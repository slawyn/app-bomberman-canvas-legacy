package main.Essentials;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import main.Globals;
import main.Constants;

public class Server extends Pipe {
    private final String LOGTAG = "BluetoothManager";
    private boolean serverrunning;
    private boolean serverlistening;
    private Connection[] connections;
    private BluetoothServerSocket serverSocket;

    public Server(Pipe pipe) {

        // Connect the Pipe to App
        pipeOut = pipe.getPipeOut();
        pipeIn = pipe.getPipeIn();
        serverrunning = false;
        serverlistening = false;
        // Init Operations
        connections = new Connection[Constants.MAX_NUMBER_OF_PLAYERS - 1];
        new Thread(serverListener).start();
    }

    /* https://stackoverflow.com/questions/35953413/bluetooth-connect-without-pairing */
    private Runnable serverListener = new Runnable() {
        public void run() {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.setName(Constants.SERVER_NAME);
            UUID uuid = UUID.fromString(Constants.SERVER_UUID);

            try {
                int numOfClients = 0;
                                // TODO add choice to select secure and unsecure
                                //listenUsingRfcommWithServiceRecord
                serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("BomberServer", uuid);

                Log.e(LOGTAG, "Listening...");
                serverrunning = true;

                // Listen for incoming connections
                BluetoothSocket socket;
                serverlistening = true;

                // Count server as player
                Globals.numberOfPlayers++;

                while (serverlistening) {
                    socket = serverSocket.accept();

                    if (numOfClients < connections.length) {

                        // Spawn Connection
                        connections[numOfClients] = new Connection(socket, ++numOfClients);

                        // Log
                        Log.e(LOGTAG, "Socket accepted..." + (numOfClients));

                        Globals.numberOfPlayers++;

                        // Everybody has connected
                        if(Globals.numberOfPlayers == Constants.MAX_NUMBER_OF_PLAYERS) {
                            serverlistening = false;
                            new Thread(writingThread, "##Server Writing Thread").start();
                            Log.e(LOGTAG, " Dropping listener");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable writingThread = new Runnable() {

        @Override
        public void run() {

            boolean allHaveBeenApproved;
            try {

                // Sent IDS together with number of Clients
                byte b [] = new byte []{0,Constants.PROTOCOL_ID_DISTRIBUTION,(byte)(Globals.numberOfPlayers),0,0};
                // sent the number of clients
                for (int i = 0; i < connections.length; i++) {
                    b[0] = (byte)connections[i].getID();
                    connections[i].sendData(b);
                }

                // Wait for IDs to come back, and approve all Connections
                // Synchronization
                while (true) {
                    allHaveBeenApproved = true;
                    for (int i = 0; i < connections.length; i++) {
                        if (!connections[i].approved && connections[i].hasData()) {
                            allHaveBeenApproved = false;
                            byte[] data = connections[i].receivedData.remove();
                            if (data[1] == Constants.PROTOCOL_ID_DISTRIBUTION) {
                                connections[i].approved = true;
                            }
                        }
                    }

                    if (allHaveBeenApproved)
                        break;
                    Thread.sleep(1);
                }

                Log.e(LOGTAG,"All Connections approved");
                /* Server exchange*/
                while (serverrunning) {

                    /* Process incoming network data*/
                    for (int i = 0; i < connections.length; i++) {
                        if (connections[i] == null) {
                            continue;
                        }
                        if (connections[i].hasData()) {
                            pipeIn.add(connections[i].receivedData.remove());
                        }
                    }

                    /* Send state/time data to players*/
                    if (!pipeOut.isEmpty()) {
                        byte[] data = pipeOut.remove();

                        long t  = SystemClock.elapsedRealtime();
                        data[10] = (byte) (t);
                        data[11] = (byte) (t >> 8);
                        data[12] = (byte) (t >> 16);
                        data[13] = (byte) (t >> 24);
                        data[14] = (byte) (t >> 32);
                        data[15] = (byte) (t >> 40);
                        data[16] = (byte) (t >> 48);
                        data[17] = (byte) (t >> 56);
                        // TODO do I need to check for type of frame herer?
                        for (int i = 0; i < connections.length; i++) {
                            connections[i].sendData(data);
                        }
                    } else {
                        Thread.sleep(1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void closeConnection() {

        /* Close connections */
        for (Connection connection : connections) {
            connection.close();
        }

        /* Close server socket*/
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        serverrunning = false;

    }
}

