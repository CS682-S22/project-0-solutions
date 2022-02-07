package models;

import configurations.Config;
import configurations.Destination;
import controllers.Connection;
import controllers.LossyConnection;

import java.net.Socket;

/**
 * Responsible for creating new connection between two hosts.
 * It can create two types of connections:
 *      1) Default connection: Messages may lose because of network issues
 *      2) Lossy connection: Few messages can lose or messages will be delayed
 *
 */
public class Connections {

    private Connections() {}

    public static Connection getConnection(Socket channel, Config config, Destination destination) {
        Connection connection;

        if(config.getDelay() > 0 || config.getLossRate() > 0) {
            connection = new LossyConnection(channel, destination.getMsgCount(), config.getLossRate(), config.getDelay(), destination.getAddress(), destination.getPort(), config.getSource().getAddress(), config.getSource().getPort());
        } else {
            connection = new Connection(channel, destination.getMsgCount(), destination.getAddress(), destination.getPort(), config.getSource().getAddress(), config.getSource().getPort());
        }

        return connection;
    }
}
