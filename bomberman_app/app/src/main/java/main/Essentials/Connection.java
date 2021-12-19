package main.Essentials;

import android.bluetooth.BluetoothSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.Constants;
import main.Globals;

public class Connection {
    private final String LOGTAG = "Connection";
    private BluetoothSocket socket;
    public boolean approved;
    private DataInputStream is;
    private DataOutputStream os;
    public Queue<byte[]> receivedData;
    private boolean connectionAlive;
    private int identification;
    private byte buffer[][];
    private int idx;
    public Connection(BluetoothSocket sock, int id) {
        socket = sock;

        try {
            identification = id;
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            connectionAlive = true;
            receivedData = new ConcurrentLinkedQueue<>();
            approved = false;
            idx = 0;
            buffer = new byte[10][Constants.MAX_PACKET_SIZE];
            new Thread(readingThread, "## Server Reading Thread").start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getID(){
        return identification;
    }

    public boolean hasData() {
        return !receivedData.isEmpty();
    }

    public void sendData(byte[] state) throws IOException {
        Globals.numberOfSentPackets++;
        os.write(state);
        os.flush();
    }

    public void close() {
        try {
            connectionAlive = false;
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable readingThread = new Runnable() {

        @Override
        public void run() {
            try {
                while (connectionAlive) {

                    if (is.read(buffer[idx], 0, Constants.MAX_PACKET_SIZE) != -1) {
                        /* Accept only packets with valid ID */
                        if (buffer[idx][0] == identification) {
                            Globals.numberOfReceivedPackets++;
                            receivedData.add(buffer[idx]);
                        }
                    } else {
                        Thread.sleep(1);
                    }
                    idx = (idx+1)%buffer.length;

                    // TODO parse frame, already here
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
}