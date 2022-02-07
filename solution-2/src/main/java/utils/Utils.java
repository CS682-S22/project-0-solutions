/**
 * This class is for generic utilities
 *
 * The timeDiff function is from my project2 from cs601
 *
 */
package utils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Generic utility class
 */
public class Utils {

  /**
   * Sleeps for a specified time
   * @param milli time to sleep
   */
  public static void sleep(int milli) {
    try {
      Thread.sleep(milli);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * This is from my project2 from cs601, this function is modified to return the number of seconds
   *
   * Computes the difference in min and seconds between two LocalDateTime objects. This is used
   * to time how long a series of steps takes to run.
   *
   * @param timeBefore The first LocalDateTime object
   * @param timeAfter the second LocalDateTime object
   * @return The difference in min and sec between each LocalDateTime
   */
  public static double timeDiff(LocalDateTime timeBefore, LocalDateTime timeAfter) {
    // Reviewed how to use Duration class from
    // https://docs.oracle.com/javase/tutorial/datetime/iso/period.html
    long milliseconds = Duration.between(timeBefore, timeAfter).toMillis();
    long min = milliseconds / 60000;
    return (milliseconds - (min * 60000)) / (double) 1000;
  }
}
