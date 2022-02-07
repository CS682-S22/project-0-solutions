/**
 * This class is for generating random numbers
 */
package utils;

import java.util.Random;

/**
 * The RandomNumbers class for generating numbers
 */
public class RandomNumbers {
  private Random random;

  public RandomNumbers() {
    this.random = new Random();
  }

  /**
   * Gets a random number between 0 and max
   * @param max the max number
   * @return the number between 0 and max
   */
  protected int randomNumber(int max) {
    return this.random.nextInt(max) + 1;
  }

  /**
   * Computes if a failure should occur based on a fail rate.
   * The fail rate is a number between 0 and 1
   * A fail rate of 0.1 indicates that the statistical likelihood of a failure is 10%
   *
   * @param failRate The fail rate
   * @return true if the operation should succeed and false otherwise
   */
  public boolean controlledFailure(double failRate) {
    double comparison = failRate * PeerConstants.ONE_HUNDRED_PERCENT;
    int randNum = this.randomNumber(PeerConstants.ONE_HUNDRED_PERCENT);
    return randNum > comparison;
  }
}
