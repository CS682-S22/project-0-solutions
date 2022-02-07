package controllers;

/**
 * Interface for sending messages
 *
 */
public interface Sender {
    public boolean send(byte[] message);
}
