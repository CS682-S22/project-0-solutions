package controllers;

import com.google.protobuf.InvalidProtocolBufferException;
import configurations.Config;
import configurations.Constants;
import configurations.Destination;
import models.Connections;
import models.NodeMessage;
import utilities.Util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Responsible for listening and initiating connections with another host
 *
 */
public class NodeService {
    private ExecutorService threadPool;
    private Config config;
    private boolean running = true;

    public NodeService(Config config) {
        this.threadPool = Executors.newFixedThreadPool(Constants.numberOfThreads);
        this.config = config;
    }

    /**
     * Initiate connection with the other hosts
     */
    public void initiate() {
        for (Destination destination : config.getDestinations()) {
            boolean doAttempt = true;

            while (doAttempt) {
                try {
                    Socket socket = new Socket(destination.getAddress(), destination.getPort());
                    System.out.printf("[%s:%d] Successfully connected to host %s:%d.\n", config.getSource().getAddress(), config.getSource().getPort(), destination.getAddress(), destination.getPort());
                    Connection connection = Connections.getConnection(socket, config, destination);
                    if(connection.openConnection(Constants.communicationMethod.WRITE)) {
                        threadPool.execute(() -> startWriting(connection));
                    }
                    doAttempt = false;
                } catch (ConnectException connectException) {
                    System.err.printf("Server %s:%d is not up right now. Waiting for few seconds. \n", destination.getAddress(), destination.getPort());
                    Util.sleep(Constants.sleepTime);
                } catch (IOException exception) {
                    System.err.printf("[%s:%d] Fail to make connection with the host %s:%d. Error: %s.\n", config.getSource().getAddress(), config.getSource().getPort(), destination.getAddress(), destination.getPort(), exception.getMessage());
                    exception.printStackTrace();
                    doAttempt = false;
                }
            }
        }
    }

    /**
     * Listen for the connections from another hosts.
     */
    public void listen() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(config.getSource().getPort());
        } catch (IOException exception) {
            System.err.printf("Fail to start the server at the node %s:%d. Error: %s.\n", config.getSource().getAddress(), config.getSource().getPort(), exception.getMessage());
            return;
        }

        System.out.printf("[%s] Listening on port %d.\n", config.getSource().getAddress(), config.getSource().getPort());

        while (running) {
            try {
                Socket socket = serverSocket.accept();
                System.out.printf("[%s:%d] Connection established with the host %s:%d.\n", config.getSource().getAddress(), config.getSource().getPort(), socket.getInetAddress().getHostAddress(), socket.getPort());
                Connection connection = Connections.getConnection(socket, config, new Destination(socket.getInetAddress().getHostAddress(), socket.getPort(), 0));
                if(connection.openConnection(Constants.communicationMethod.READ)) {
                    //Spawn new thread for reading using this connection
                    threadPool.execute(() -> startReading(connection));
                }
            } catch (IOException exception) {
                System.err.printf("[%s:%d] Fail to accept the connection from another host. Error: %s.\n", config.getSource().getAddress(), config.getSource().getPort(), exception.getMessage());
            }
        }
    }

    /**
     * Start reading for messages from another host using the connection object
     * @param connection object which maintains a connection between two hosts
     */
    public void startReading(Connection connection) {
        boolean reading = true;

        while (reading) {
            byte[] buffer = connection.receive();

            if(buffer != null && buffer.length > 0) {
                try {
                    NodeMessage.Message nodeMessage = NodeMessage.Message.parseFrom(buffer);
                    if(!nodeMessage.getMsgString().equalsIgnoreCase("exit")) {
                        System.out.printf("[%s:%d] Received message \"%s\"\n", config.getSource().getAddress(), connection.getSourcePort(), nodeMessage.getMsgString());
                    } else reading = false;
                } catch (InvalidProtocolBufferException e) {
                    System.err.printf("[%s:%d] Error while decoding the message from another host %s:%d. Error: %s\n", config.getSource().getAddress(), connection.getSourcePort(), connection.getDestinationIPAddress(), connection.getDestinationPort(), e.getMessage());
                    reading = false;
                }
            } else reading = false;
        }

        //Close connection with another host
        connection.closeConnection();
    }

    /**
     * Start sending messages to all the other hosts which current host wants to send data to
     * @param connection object which maintains a connection between two hosts
     */
    public void startWriting(Connection connection) {

        for (int index = 1; index <= connection.getMsgCount(); index++) {
            NodeMessage.Message message = NodeMessage.Message.newBuilder().setMsgString(String.format(Constants.messagePrefix, index, config.getSource().getAddress(), connection.getSourcePort())).build();

            boolean isSend = connection.send(message.toByteArray());

            if(isSend) {
                System.out.printf("[%s:%d] Send message \"%s\" to the host %s:%d. \n", config.getSource().getAddress(), connection.getSourcePort(), message.getMsgString(), connection.getDestinationIPAddress(), connection.destinationPort);
            } else {
                System.err.printf("[%s:%d] Message \"%s\" is lost while sending to the host %s:%d. \n", config.getSource().getAddress(), connection.getSourcePort(), message.getMsgString(), connection.getDestinationIPAddress(), connection.destinationPort);
            }
        }

        connection.send(NodeMessage.Message.newBuilder().setMsgString(Constants.endNotifier).build().toByteArray());

        //Close connection with another host
        connection.closeConnection();
    }

    /**
     * Shutdown the thread pool which will shut down the host
     */
    public void shutdown() {
        running = false;

        threadPool.shutdown();

        try {
            if(!threadPool.awaitTermination(Constants.shutdownSleepTime, TimeUnit.MINUTES)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
