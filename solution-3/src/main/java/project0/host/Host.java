package project0.host;

import project0.Config;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class to set up a host for starting listening and making connection.
 *
 */
public class Host {
    /**
     * config class.
     */
    private final Config config;
    /**
     * server socket.
     */
    private AsynchronousServerSocketChannel server;
    /**
     * host state.
     */
    private volatile boolean isRunning;

    /**
     * Constructor.
     *
     * @param config config
     */
    public Host(Config config) {
        this.config = config;
        this.isRunning = true;
        setUpOutput(config.getOutFile());
    }

    /**
     * Method to start server socket to listen for incoming messages.
     */
    public void startServer() {
        try {
            server = AsynchronousServerSocketChannel.open()
                    .bind(new InetSocketAddress(config.getHost(), config.getHostPort()));
            System.out.println("Server started on: " + ((InetSocketAddress) server.getLocalAddress()).getHostName()
                    + ":" + ((InetSocketAddress) server.getLocalAddress()).getPort());
            server.accept(null, new CompletionHandler<>() {
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    if (server.isOpen()) {
                        server.accept(null, this);
                    }
                    Connection socket = configureConnection(result);
                    while (isRunning) {
                        if (processMessage(socket, result)) {
                            if (!socket.send("Message received".getBytes(StandardCharsets.UTF_8))) {
                                System.err.println("Acknowledgement not sent");
                            }
                        }
                    }
                    try {
                        result.close();
                    } catch (IOException e) {
                        System.err.println("close(): " + e.getMessage());
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    if (server.isOpen()) {
                        System.err.println("Fail to accept connection!");
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("startServer(Config config):" + e.getMessage());
        }
    }

    /**
     * Method to start connection with the remote host and send message read from files.
     */
    public void startClient() {
        try {
            for (int i = 0; i < config.getPeers().size(); i++) {
                AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
                socketChannel.connect(new InetSocketAddress(config.getPeers().get(i), config.getPeerPorts().get(i)), null, new CompletionHandler<>() {
                    @Override
                    public void completed(Void result, Object attachment) {
                        Connection socket = configureConnection(socketChannel);
                        readAndSend(socketChannel, socket);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        System.err.println("Fail to make connection!");
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("startClient(Config config):" + e.getMessage());
        }
    }

    /**
     * Method to close host.
     */
    public void close() {
        try {
            isRunning = false;
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            System.err.println("close(): " + e.getMessage());
        }
    }

    /**
     * Helper method to write to file.
     *
     * @param message message
     */
    private void writeToFile(String message) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(config.getOutFile(), true))) {
            bufferedWriter.write(message + "\n");
        } catch (IOException e) {
            System.err.println("bufferedwriter: " + e.getMessage());
        }
    }

    /**
     * Helper method to set up files to write to.
     *
     * @param path directory to save file and file names
     */
    private void setUpOutput(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.err.println("Can't make directory: " + path);
                System.exit(1);
            }
        }
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            System.err.println("setUpOutput(String path): " + e.getMessage());
        }
    }

    /**
     * Method to read in the text from file and send over the network.
     *
     * @param socketChannel socket
     * @param socket        connection
     */
    private void readAndSend(AsynchronousSocketChannel socketChannel, Connection socket) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(config.getInFile())))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                try { // to see the affect of concurrency
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
                if (socket.send(line.getBytes(StandardCharsets.UTF_8))) {
                    writeToFile("Message sent: " + line);
                    processMessage(socket, socketChannel);
                } else {
                    System.err.println("Message not sent: " + line);
                }
            }
            while (isRunning) {
                processMessage(socket, socketChannel);
            }
            socketChannel.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Helper method to wait for message from the connection and write it to output file.
     *
     * @param socket connection
     * @param result socket
     * @return true if message is not null and written to file, else false
     */
    private boolean processMessage(Connection socket, AsynchronousSocketChannel result) {
        byte[] message = socket.receive();
        if (message != null) {
            try {
                writeToFile("Received from: " + ((InetSocketAddress) result.getRemoteAddress()).getHostName()
                        + ":" + ((InetSocketAddress) result.getRemoteAddress()).getPort()
                        + ": " + new String(message, StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.err.println("getRemoteAddress(): " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    /**
     * Method to configure connection implementation.
     *
     * @param result socket channel
     * @return Connection
     */
    private Connection configureConnection(AsynchronousSocketChannel result) {
        if (config.getLossRate() == 0 && config.getDelay() == 0) {
            return new Connection(result);
        } else {
            return new LossyConnection(result, config.getLossRate(), config.getDelay());
        }
    }
}
