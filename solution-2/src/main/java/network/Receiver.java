/**
 * Interface for receive functionality
 *
 */
package network;

/**
 * interface to receive data
 */
public interface Receiver {

  /**
   * Receives data
   * @return a byte array with the received data. Empty if no data sent
   */
  public byte[] receive();
}
