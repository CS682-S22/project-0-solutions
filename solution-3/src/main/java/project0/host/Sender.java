package project0.host;

/**
 * An interface to specify methods to be implemented by any class implementing this interface to send data
 * over socket.
 *
 */
public interface Sender {
    /**
     * Send method.
     *
     * @return a byte array
     */
    boolean send(byte[] message);
}
