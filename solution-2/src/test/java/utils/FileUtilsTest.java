/**
 * Tests for file utils
 *
 */
package utils;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import models.Config;
import org.junit.jupiter.api.Test;

/**
 * Class to test out file utils
 */
class FileUtilsTest {

  /**
   * Tests if the parseFile function works correctly
   */
  @Test
  void testParseFile() {
    Gson gson = new Gson();
    Config config = utils.FileUtils.parseFile(gson, PeerConstants.CONFIG_PATH, Config.class);
    assertEquals(3, config.getHosts().size());
  }
}
