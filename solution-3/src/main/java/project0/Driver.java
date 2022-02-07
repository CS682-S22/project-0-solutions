package project0;

import com.google.gson.Gson;
import project0.host.Host;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main program to start the connection to receive and/or send messages over the network.
 *
 * <p>
 * Reference: https://www.baeldung.com/java-nio2-async-socket-channel
 */
public class Driver {
    /**
     * Main method to run the program based on config files.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        if (!validateArgs(args)) {
            System.exit(1);
        }
        try {
            Gson gson = new Gson();
            Config config = gson.fromJson(new FileReader(args[0]), Config.class);
            Host host = new Host(config);
            if (!config.validateConfig()) {
                System.exit(1);
            }
            if (config.getHost() != null) {
                host.startServer();
            }
            if (config.getPeers() != null) {
                host.startClient();
            }
            while (true) {
                Scanner scanner = new Scanner(System.in);
                if (scanner.nextLine().equalsIgnoreCase("exit")) {
                    host.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("main(String[] args): " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Method to validate program arguments.
     *
     * @param args arguments
     * @return true if arguments include the correct config file
     */
    private static boolean validateArgs(String[] args) {
        return args.length == 1 && (new File(args[0])).exists();
    }

}
