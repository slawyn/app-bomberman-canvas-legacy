package main.Essentials;

import java.util.Queue;

public abstract class Pipe {
    protected Queue<byte[]> pipeIn;
    protected Queue<byte[]> pipeOut;

    public Queue<byte[]> getPipeIn() {
        return pipeIn;
    }

    public Queue<byte[]> getPipeOut() {
        return pipeOut;
    }
}
