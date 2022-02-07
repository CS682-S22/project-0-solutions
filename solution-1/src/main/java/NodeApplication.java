import configurations.Config;
import configurations.Constants;
import configurations.Destination;
import controllers.NodeService;
import utilities.JSONManager;
import utilities.Strings;
import utilities.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Application layer which starts the host.
 *
 */
public class NodeApplication {
    Config configuration;

    public static void main(String[] args) {
        NodeApplication nodeApplication = new NodeApplication();
        String configFileLocation = nodeApplication.getConfig(args);

        if(!Strings.isNullOrEmpty(configFileLocation)) {
            nodeApplication.readConfig(configFileLocation);

            boolean isValid = nodeApplication.verifyConfig();

            if(isValid) {
                nodeApplication.startServer();
                Util.sleep(Constants.sleepTimeBeforeInitiating);
                nodeApplication.startClient();
            }
        }
    }

    /**
     * Read comment line arguments and return the location of the configuration file.
     * @param args Command line arguments being passed when running a program
     * @return location of configuration file if passed arguments are valid else null
     */
    private String getConfig(String[] args) {
        String configFileLocation = null;

        if(args.length == 2 &&
                args[0].equalsIgnoreCase("-config") &&
                !Strings.isNullOrEmpty(args[1])) {
            configFileLocation = args[1];
        }
        else {
            System.out.println("Invalid Arguments");
        }

        return configFileLocation;
    }

    /**
     * Parse configuration file.
     * @param configFileLocation location of configuration file
     */
    private void readConfig(String configFileLocation) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(configFileLocation))){
            configuration = JSONManager.fromJson(reader, Config.class);
        }
        catch (IOException ioException) {
            StringWriter writer = new StringWriter();
            ioException.printStackTrace(new PrintWriter(writer));

            System.out.printf("Unable to open configuration file at location %s. %s. \n", configFileLocation, writer);
        }
    }

    /**
     * Verifies if the config is valid or not
     * @return true if valid else false
     */
    public boolean verifyConfig() {
        boolean flag = false;

        if(configuration == null) {
            System.out.println("No configuration found.");
        }
        else if(configuration.getSource() == null){
            System.out.println("No source configuration provided");
        } else if(Strings.isNullOrEmpty(configuration.getSource().getAddress())) {
            System.out.println("No source address mentioned");
        } else if(configuration.getSource().getPort() <= 0) {
            System.out.println("Incorrect source port found");
        } else if(configuration.getLossRate() < 0 || configuration.getLossRate() > 1) {
            System.out.println("Loss rate must be between 0 and 1");
        } else if(configuration.getDestinations().size() > 0) {
            for (Destination destination : configuration.getDestinations()) {
                if(Strings.isNullOrEmpty(destination.getAddress()) || destination.getPort() <= 0 || destination.getMsgCount() <= 0) {
                    System.out.println("Incorrect destination information");
                    flag = false;
                    break;
                } else {
                    flag = true;
                }
            }
        } else {
            flag = true;
        }

        return flag;
    }

    /**
     * Start the server side of the host
     */
    private void startServer() {
        NodeService node = new NodeService(configuration);
        Thread thread = new Thread(node::listen);
        thread.start();
    }

    /**
     * Start the client side of the host
     */
    private void startClient() {
        NodeService client = new NodeService(configuration);
        Thread thread = new Thread(client::initiate);
        thread.start();
    }
}
