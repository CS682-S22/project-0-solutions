/**
 * This class is a connection class that makes a connection between peers and sends and receives data
 *
 * Reviewed project 3 from cs601 on how to create a reader/writer from a socket
 *
 */
package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import models.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Connection class for making connections
 */
public class Connection implements Receiver, Sender {
  protected Config config;
  private DataInputStream input;
  private DataOutputStream output;

  private final static int NUM_RETRIES = 4;
  private static final Logger logger = LogManager.getLogger(Connection.class);

  public Connection(Config config, Socket socket) {
    this.config = config;
    try  {
      /*
      Reviewed this resource on how to read in bytes in an input stream from a socket
      https://www.baeldung.com/java-inputstream-server-socket
      */
      this.input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public byte[] receive() {
    /*
      Reviewed this resource on how to read in bytes in an input stream from a socket
      https://www.baeldung.com/java-inputstream-server-socket
     */
    try {
      int i;
      for (i = 0; i < NUM_RETRIES; i++) {
        try {
          int byteLength = this.input.readInt();
          byte[] message = new byte[byteLength];
          input.readFully(message);
          return message;
        } catch (java.io.EOFException e) {
          logger.trace(String.format("Receive retry number: %d", i));
        }
      }
    } catch (IOException e) {
      System.out.println("Unable to receive message");
    }
    return new byte[0];
  }

  @Override
  public boolean send(byte[] message) {
    /*
      Reviewed this resource on how to send bytes over a socket
      https://www.baeldung.com/java-inputstream-server-socket
     */
    try {
      this.output.writeInt(message.length);
      this.output.write(message);
      this.output.flush();
    } catch (IOException e) {
      return false;
    }
    return true;
  }
}
