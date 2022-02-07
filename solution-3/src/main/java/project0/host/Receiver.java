package project0.host;

/**
 * An interface to specify methods to be implemented by any class implementing this interface to receive data
 * over socket.
 *
 */
public interface Receiver {
    /**
     * Receive method.
     *
     * @return a byte array
     */
    byte[] receive();
}
