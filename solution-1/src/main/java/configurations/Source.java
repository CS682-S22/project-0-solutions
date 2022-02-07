package configurations;

/**
 * Responsible for storing source information
 *
 */
public class Source {
    private String address;
    private int port;

    public Source(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * @return Gets the address of the source host
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return Get the port number of the source host at which it is listening
     */
    public int getPort() {
        return port;
    }
}
