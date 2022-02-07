/**
 * This contains tests for generic util functions
 *
 */
package utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * Class for testing utilities
 */
class UtilsTest {

  /**
   * Tests if the sleep function works correctly
   */
  @Test
  void testSleepTenSec() {
    int sleepDuration = 3000;
    LocalDateTime start = LocalDateTime.now();
    Utils.sleep(sleepDuration);
    LocalDateTime end = LocalDateTime.now();
    double elapsedTime = Utils.timeDiff(start, end);
    assertTrue(elapsedTime >= sleepDuration / 1000);
  }
}
