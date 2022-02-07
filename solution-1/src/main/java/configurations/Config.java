package configurations;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for holding all the inputs required for the host to run
 *
 */
public class Config {
    private List<Destination> destinations;
    private float lossRate;
    private int delay;
    private Source source;

    public Config() {
        this.destinations = new ArrayList<>();
    }

    /**
     * @return all the destinations
     */
    public List<Destination> getDestinations() {
        return destinations;
    }

    /**
     * @return loss rate
     */
    public float getLossRate() {
        return lossRate;
    }

    /**
     * @return time in milliseconds
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @return local address information
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the local address information
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * Sets the time in milliseconds
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Sets the loss rate
     */
    public void setLossRate(float lossRate) {
        this.lossRate = lossRate;
    }

    /**
     * Add destination information to the collection
     */
    public void addDestination(Destination destination) {
        destinations.add(destination);
    }
}

