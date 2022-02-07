/**
 * Interface for send functionality
 *
 */
package network;

/**
 * Sender interface for sending data
 */
public interface Sender {

  /**
   * Sends data across a network
   * @param message the data to send
   * @return true if sent, false otherwise
   */
  public boolean send(byte[] message);
}
