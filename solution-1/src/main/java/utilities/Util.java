package utilities;

/**
 * Responsible for having utility functions which can be referenced in multiple places.
 *
 */
public class Util {

    /**
     * Sleeping current thread for the mentioned time
     * @param milliseconds time unit in millisecond
     */
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.err.printf("Error while sleeping for %d milliseconds. Error: %s.\n", milliseconds, e.getMessage());
        }
    }
}
