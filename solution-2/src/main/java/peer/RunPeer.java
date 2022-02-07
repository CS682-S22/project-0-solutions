/**
 * This class is for setting up a peer and running it
 *
 * I reviewed this class in project 3 from cs601 on setting up a thread pool connection and ServerSocket
 *
 */
package peer;

import network.Connection;
import network.PoorConnection;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import messaging.protos.MessageInfo;
import messaging.protos.MessageInfo.Message;
import models.Config;
import models.Config.Hosts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Utils;

/**
 * Class for running a peer
 */
public class RunPeer {
  private Config config;
  private String hostId;
  private int port;
  private volatile boolean running = true;
  private ServerSocket serverSocket;
  private ExecutorService threadPool;

  private static final Logger logger = LogManager.getLogger(RunPeer.class);
  private final int NUM_THREADS  = 100;
  private final int SLEEP_MILLI = 10000;
  private final int SLEEP_TEN = 1000;

  public RunPeer(Config config, String hostId) {
    this.config = config;
    this.hostId = hostId;
    this.port = this.config.getHosts().stream()
        .filter((Hosts h) -> h.getId().equals(this.hostId))
        .findAny().get().getPort();
    this.threadPool = Executors.newFixedThreadPool(NUM_THREADS);
  }

  /**
   * Function to start the peer
   * Sets up a listener to listen for incoming connections and then searches to see if any other hosts are available to connect
   */
  public void start() {
    logger.info(this.hostId);
    logger.info(String.format("Starting peer as host: %s running on port: %d", this.hostId, this.port));
    if (config.isLossy()) {
      logger.info(String.format("Running connections with loss rate of: %.1f", this.config.getLossRate()));
    }
    if (config.isDelay()) {
      logger.info(String.format("Running connections with delay rate of: %d", this.config.getDelayRate()));
    }

    startAcceptor();

    // sleep to allow other servers to also start
    logger.info(String.format("Sleeping for %d seconds", SLEEP_MILLI / 1000));
    Utils.sleep(SLEEP_MILLI);

    startInitiator();
  }

  /**
   * This starts the acceptor that starts accepting connections from other peers
   */
  private void startAcceptor() {
    try {
      this.serverSocket = new ServerSocket(this.port);
    } catch (IOException e) {
      logger.info(String.format("Unable to start server on port %d", this.port));
      e.printStackTrace();
    }
    logger.info("Starting to listen for other connections");
    Thread listener = new Thread(() -> {
    while (this.running) {
      try {
        Socket socket = this.serverSocket.accept();
        logger.info("connected to new peer");
        Connection connection = getConnection(socket);
        this.threadPool.execute(() -> send(this.hostId, connection));
        System.out.println("created a new connection for the listener");
        this.threadPool.execute(() -> receive(connection));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    });
    listener.start();
  }

  /**
   * This starts the initiator that initiates connections to other peers
   */
  private void startInitiator() {
    config.getHosts().stream()
        .filter((Hosts filterHosts) -> !filterHosts.getId().equals(this.hostId))
        .forEach((Hosts h) -> {
          try {
            Socket clientSocket;
            clientSocket = new Socket(h.getHost(), h.getPort());
            logger.info(String.format("Connection established to peer %s on port %d", h.getHost(), h.getPort()));
            Connection connection = getConnection(clientSocket);
            this.threadPool.execute(() -> send(this.hostId, connection));
            this.threadPool.execute(() -> receive(connection));
          } catch (IOException e) {
            logger.error(String.format("Unable to make sent connection to peer %s on port %d", h.getHost(), h.getPort()));
          }
        });
  }

  /**
   * Receives data based on the connection
   * @param connection The connection to use to receive data
   */
  private void receive(Connection connection) {
    for (int i = 1; i < 25; i++) {
      byte[] messageBytes = connection.receive();
      if (messageBytes.length == 0) {
        System.out.println("Failed to receive message");
      } else {
        try {
          Message message = MessageInfo.Message.parseFrom(messageBytes);
          System.out.printf("received message: %s\n", message.getText());
        } catch (InvalidProtocolBufferException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Sends data for the connection
   * @param hostId The host id to print out
   * @param connection The connection to use to send the data
   */
  private void send(String hostId, Connection connection) {
    for (int i = 1; i < 25; i++) {
      byte[] message = MessageInfo.Message.newBuilder().setText(String.format("%d from %s", i, hostId)).build().toByteArray();
      boolean success = connection.send(message);
      if (success) {
        System.out.printf("sending message: %d from %s\n", i, hostId);
      } else {
        System.out.printf("Failed to send message: %d from %s\n", i, hostId);
      }
      Utils.sleep(SLEEP_TEN);
    }
  }

  /**
   * Sets up the correct connection class to use
   * @param socket The socket to use for the connection
   * @return the connection
   */
  private Connection getConnection(Socket socket) {
    if (this.config.isLossy() || this.config.isDelay()) {
      return new PoorConnection(this.config, socket);
    } else {
      return new Connection(this.config, socket);
    }
  }
}
