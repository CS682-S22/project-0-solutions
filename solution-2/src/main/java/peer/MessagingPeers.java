/**
 * This is the main function to run the peer messaging application
 *
 *  I reviewed project 3 from cs601 on how to set up the logger within this class
 *
 */
package peer;

import com.google.gson.Gson;
import models.Config;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.PeerConstants;

public class MessagingPeers {

  /*
  Reviewed project 3 from cs601 on how to set up the apache logger
   */
  private static final Logger logger = LogManager.getLogger(MessagingPeers.class);
  public static void main(String[] args) {
    if (args.length < 2 || !args[0].equals("-h")) {
      logger.log(Level.ERROR, "Invalid args...exiting now");
      System.exit(1);
    }
    String hostId = args[1];

    Gson gson = new Gson();
    Config config = utils.FileUtils.parseFile(gson, PeerConstants.CONFIG_PATH, Config.class);
    RunPeer runPeer = new RunPeer(config, hostId);
    runPeer.start();
  }
}
