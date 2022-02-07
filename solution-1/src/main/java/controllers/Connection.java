package controllers;

import configurations.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Responsible for sending and receiving data from the channel being established between two hosts.
 *
 */
public class Connection implements Sender, Receiver {

    private Socket channel;;
    protected int msgCount;
    protected String destinationIPAddress;
    protected int destinationPort;
    protected String sourceIPAddress;
    protected int sourcePort;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public Connection(Socket channel, int msgCount, String destinationIPAddress, int destinationPort, String sourceIPAddress, int sourcePort) {
        this.channel = channel;
        this.msgCount = msgCount;
        this.destinationIPAddress = destinationIPAddress;
        this.destinationPort = destinationPort;
        this.sourceIPAddress = sourceIPAddress;
        this.sourcePort = sourcePort;
    }

    public Connection() {}

    /**
     * Opens the connection with another host by opening the input stream or output stream based on the requirement
     * @param communicationMethod Enum with two valued READ and WRITE
     * @return true if successful else false
     */
    public boolean openConnection(Constants.communicationMethod communicationMethod) {
        boolean isSuccess = false;

        try {
            if (communicationMethod == Constants.communicationMethod.READ) {
                inputStream = new DataInputStream(channel.getInputStream());
            } else if (communicationMethod == Constants.communicationMethod.WRITE) {
                outputStream = new DataOutputStream(channel.getOutputStream());
            }
            isSuccess = true;
        } catch (IOException exception) {
            System.err.printf("[%s:%d] Unable to get input/output stream. Error: %s.\n", sourceIPAddress, sourcePort, exception.getMessage());
        }

        return isSuccess;
    }

    /**
     * @return the destination host address
     */
    public String getDestinationIPAddress() {
        return destinationIPAddress;
    }

    /**
     * @return the destination port number
     */
    public int getDestinationPort() {
        return destinationPort;
    }

    /**
     * @return the source port
     */
    public int getSourcePort() {
        return sourcePort;
    }

    /**
     * @return number of messages will pass through the channel (0 in case of reading)
     */
    public int getMsgCount() {
        return msgCount;
    }

    /**
     * Sending message to another host
     * @param message
     * @return true if successful else false
     */
    @Override
    public boolean send(byte[] message) {
        boolean isSend = false;

        try {
            outputStream.writeInt(message.length);
            outputStream.write(message);
            isSend = true;
        } catch (IOException exception) {
            System.err.printf("[%s:%d] Fail to send message to %s:%d. Error: %s.\n", sourceIPAddress, sourcePort, destinationIPAddress, destinationPort, exception.getMessage());
        }

        return isSend;
    }

    /**
     * Receive message from another host.
     * @return the message being received
     */
    @Override
    public byte[] receive() {
        byte[] buffer = null;

        try {
            int length = inputStream.readInt();
            if(length > 0) {
                buffer = new byte[length];
                inputStream.readFully(buffer, 0, buffer.length);
            }
        } catch (EOFException ignored) {} //No more content available to read
        catch (IOException exception) {
            System.err.printf("[%s:%d] Fail to receive message from %s:%d. Error: %s.\n", sourceIPAddress, sourcePort, destinationIPAddress, destinationPort, exception.getMessage());
        }

        return buffer;
    }

    /**
     * CLose the connection between two hosts
     */
    public void closeConnection() {
        try {
            if(inputStream != null) inputStream.close();
            if(outputStream != null) outputStream.close();
            channel.close();
        } catch (IOException e) {
            System.err.printf("[%s:%d] Unable to close the connection. Error: %s", sourceIPAddress, sourcePort, e.getMessage());
        }
    }
}
