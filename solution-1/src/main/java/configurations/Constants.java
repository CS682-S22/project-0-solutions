package configurations;

/**
 * Responsible for declaring constants
 *
 */
public class Constants {
    public static int numberOfThreads = 10;
    public static int sleepTime = 1000;
    public static int sleepTimeBeforeInitiating = 5000;
    public static int shutdownSleepTime = 2;

    public static String messagePrefix = "Message%d from host %s:%d";
    public static String endNotifier = "exit";

    public enum communicationMethod {
        READ,
        WRITE
    }
}
