package configurations;

/**
 * Responsible for holding destination information
 *
 */
public class Destination {
    private String address;
    private int port;
    private int msgCount;

    /**
     * @return the host address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the number of messages to send
     */
    public int getMsgCount() {
        return msgCount;
    }

    public Destination(String address, int port, int msgCount) {
        this.address = address;
        this.port = port;
        this.msgCount = msgCount;
    }
}
