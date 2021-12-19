package main.wrapper;

import main.Essentials.Pipe;

public interface Connector {
    boolean hasDevices();
    String discoveredDevice();
    void discoverDevices();
    void stopDiscovery();
    boolean isBluetoothEnabled();
    void makeBluetoothDiscoverable();
    int getAvailableMemory();
    boolean connectPipeToServer(Pipe pipe);
    boolean connectPipeToClient(String name, Pipe pipe);
}
