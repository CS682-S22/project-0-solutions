/**
 * System test to run the whole system in a system test
 *
 *  I reviewed this resource on how to capture the standard out as a string. Some of the code in this class was referenced from this resource and cited per instance
 *  https://www.baeldung.com/java-testing-system-out-println#working-with-core-java
 *
 */
package peer;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import models.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.PeerConstants;
import utils.Utils;

/**
 * Runs the system for two hosts in separate threads and tests the output
 */
class RunPeerTest {
  private static final String H1 = "h1";
  private static final String H2 = "h2";
  private static final String MESSAGE_1 = "received message: 24 from h1";
  private static final String MESSAGE_2 = "received message: 24 from h2";

  /**
   I reviewed this resource on how to capture the standard out as a string as a ByteArrayOutputStream
   https://www.baeldung.com/java-testing-system-out-println#working-with-core-java
   */
  private final PrintStream out = System.out;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  /*
    I reviewed this resource on how to capture the standard out as a string with a ByteArrayOutputStream
    https://www.baeldung.com/java-testing-system-out-println#working-with-core-java
   */
  @BeforeEach
  public void setUp() {
    System.setOut(new PrintStream(outputStream));
  }

  /**
   * System test to run two hosts on localhost and then check if the last message was sent correctly
   */
  @Test
  void testSystem() {
    Gson gson = new Gson();
    Config config = utils.FileUtils.parseFile(gson, PeerConstants.CONFIG_PATH, Config.class);

    Thread h1 = new Thread(() -> {
      RunPeer runPeer = new RunPeer(config, H1);
      runPeer.start();
    });

    Thread h2 = new Thread(() -> {
      RunPeer runPeer = new RunPeer(config, H2);
      runPeer.start();
    });
    assertTrue(true);
    h1.start();
    h2.start();

    try {
      h1.join();
      h2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Utils.sleep(30000);

    /*
    I reviewed this resource on how to capture the standard out as a string.
    outputStream.toString() gets the output buffer as a string
    https://www.baeldung.com/java-testing-system-out-println#working-with-core-java
     */
    assertTrue(outputStream.toString().contains(MESSAGE_1));
    assertTrue(outputStream.toString().contains(MESSAGE_2));
  }

  /*
  I reviewed this resource on how to capture the standard out as a string.
  This line sets standard out back to System.out
  https://www.baeldung.com/java-testing-system-out-println#working-with-core-java
 */
  @AfterEach
  public void tearDown() {
    System.setOut(out);
  }
}
