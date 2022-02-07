package project0;

import java.io.File;
import java.util.List;

/**
 * Class that holds config info.
 *
 */
public class Config {
    /**
     * server name.
     */
    private final String host;
    /**
     * server port.
     */
    private final int hostPort;
    /**
     * list of peer's names.
     */
    private final List<String> peers;
    /**
     * list of peer's ports.
     */
    private final List<Integer> peerPorts;
    /**
     * loss rate.
     */
    private final double lossRate;
    /**
     * delay parameter.
     */
    private final int delay;
    /**
     * file name to read from and send to server.
     */
    private final String inFile;
    /**
     * file name to output message.
     */
    private final String outFile;

    /**
     * Constructor.
     *
     * @param host      server name
     * @param hostPort  server port
     * @param peers     list of peer's names
     * @param peerPorts list of peer's ports
     * @param lossRate  loss rate
     * @param delay     delay parameter
     * @param inFile    file name to read from and send to server
     * @param outFile   file name to output message
     */
    public Config(String host, int hostPort, List<String> peers, List<Integer> peerPorts, double lossRate,
                  int delay, String inFile, String outFile) {
        this.host = host;
        this.hostPort = hostPort;
        this.peers = peers;
        this.peerPorts = peerPorts;
        this.lossRate = lossRate;
        this.delay = delay;
        this.inFile = inFile;
        this.outFile = outFile;
    }

    /**
     * Getter for server name.
     *
     * @return server name
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter for server port.
     *
     * @return server port
     */
    public int getHostPort() {
        return hostPort;
    }

    /**
     * Getter for list of peer's names.
     *
     * @return list of peer's names
     */
    public List<String> getPeers() {
        return peers;
    }

    /**
     * Getter for list of peer's ports.
     *
     * @return list of peer's ports
     */
    public List<Integer> getPeerPorts() {
        return peerPorts;
    }

    /**
     * Getter for loss rate.
     *
     * @return loss rate
     */
    public double getLossRate() {
        return lossRate;
    }

    /**
     * Getter for delay parameter.
     *
     * @return delay parameter
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Getter for file name to read from and send to server.
     *
     * @return file name
     */
    public String getInFile() {
        return inFile;
    }

    /**
     * Getter for file name to output message.
     */
    public String getOutFile() {
        return outFile;
    }

    /**
     * Method to validate configuration.
     *
     * @return true if valid, else false
     */
    public boolean validateConfig() {
        if (host == null && peers == null || hostPort == 0 && peerPorts == null) {
            System.out.println("Missing project0.host and peer info. Need at least one of them!");
            return false;
        }
        if (outFile == null) {
            System.out.println("Need to specify log file");
            return false;
        }
        if (host != null && hostPort == 0 || host == null && hostPort != 0) {
            System.out.println("Missing project0.host port number or project0.host port name!");
            return false;
        }
        if (peers != null && peerPorts == null || peers == null & peerPorts != null) {
            System.out.println("Missing peer port number or peer port name!");
            return false;
        }
        if (peers != null && (inFile == null || !(new File(inFile).exists()))) {
            System.out.println("Need to verify file name!");
            return false;
        }
        if (lossRate < 0 || lossRate > 1 || delay < 0) {
            System.out.println("Invalid loss rate and/or delay parameter!");
            return false;
        }
        return true;
    }

}
