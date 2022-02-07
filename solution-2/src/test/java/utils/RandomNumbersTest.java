/**
 * This contains tests for random number functions
 *
 */
package utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * RandomNumberTest for testing random number functions
 *
 */
class RandomNumbersTest {

  /**
   * Tests if the randomNumber method returns a number between 0 and max
   */
  @Test
  void testRandomNumberMax() {
    RandomNumbers randomNumbers = new RandomNumbers();
    int max = 100;
    for (int i = 0; i < 300; i++) {
      int num = randomNumbers.randomNumber(max);

      assertTrue(num <= max);
      assertTrue(num > 0);
    }
  }

  /**
   * Tests if a failRate of 1.0 will always fail
   */
  @Test
  void testControlledFailure() {
    RandomNumbers randomNumbers = new RandomNumbers();
    double failRate = 1.0;
    for (int i = 0; i < 300; i++) {
      boolean success = randomNumbers.controlledFailure(failRate);
      assertFalse(success);
    }
  }

  /**
   * Tests if a fail rate of 0.0 will always succeed
   */
  @Test
  void testControlledSuccess() {
    RandomNumbers randomNumbers = new RandomNumbers();
    double failRate = 0.0;
    for (int i = 0; i < 300; i++) {
      boolean success = randomNumbers.controlledFailure(failRate);
      assertTrue(success);
    }
  }
}
