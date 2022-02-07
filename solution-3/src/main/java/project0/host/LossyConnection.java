package project0.host;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Random;

/**
 * A subclass of the Connection class that inject failure into the connection (delay and loss).
 *
 */
public class LossyConnection extends Connection {
    /**
     * the loss rate.
     */
    private final double lossRate;
    /**
     * the delay parameter.
     */
    private final int delay;
    /**
     * the random class.
     */
    private final Random random;

    /**
     * Constructor.
     *
     * @param socketChannel the socket channel
     * @param lossRate      the loss rate
     * @param delay         the delay parameter
     */
    public LossyConnection(AsynchronousSocketChannel socketChannel, double lossRate, int delay) {
        super(socketChannel);
        this.lossRate = lossRate;
        this.delay = delay;
        this.random = new Random();
    }

    /**
     * Method to send message over the network with delay and loss.
     *
     * @param message byte array
     * @return true if message sent, else false
     */
    @Override
    public boolean send(byte[] message) {
        double chance = random.nextDouble();
        if (chance > lossRate || chance == 0 && lossRate == 0) {
            processDelay(delay);
            return super.send(message);
        }
        return false;
    }

    /**
     * Method to receive message over the network with delay and loss.
     *
     * @return the byte array of message or null
     */
    @Override
    public byte[] receive() {
        double chance = random.nextDouble();
        if (chance > lossRate || chance == 0 && lossRate == 0) {
            processDelay(delay);
            return super.receive();
        }
        return null;
    }

    /**
     * Helper method to generate actual delay time.
     *
     * @param delayBound bound of delay
     */
    private void processDelay(int delayBound) {
        if (delayBound != 0) {
            int actualDelay = random.nextInt(delayBound);
            try {
                Thread.sleep(actualDelay);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
