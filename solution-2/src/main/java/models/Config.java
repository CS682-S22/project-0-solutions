/**
 * Class for config.json
 *
 */
package models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Config class to open config.json in memory
 */
public class Config {
  private ArrayList<Hosts> hosts;
  private boolean lossy;
  private boolean delay;

  /**
   * Reviewed this resource on how to have a different name in the json file vs class file
   * https://howtodoinjava.com/gson/gson-serializedname/
   */
  @SerializedName(value = "delay_rate")
  private int delayRate;

  @SerializedName(value = "loss_rate")
  private double lossRate;

  /**
   * Gets the delay rate
   * @return the delay rate
   */
  public int getDelayRate() {
    return this.delayRate;
  }

  /**
   * Gets the loss rate
   * @return the loss rate
   */
  public double getLossRate() {
    return this.lossRate;
  }

  /**
   * Gets the array of hosts
   * @return the array list of hosts
   */
  public ArrayList<Hosts> getHosts() {
    return this.hosts;
  }

  /**
   * Gets if the connection should be lossy
   * @return true if lossy false otherwise
   */
  public boolean isLossy() {
    return this.lossy;
  }

  /**
   * gets if delay is set to false or true
   * @return returns true if delayed false otherwise
   */
  public boolean isDelay() {
    return this.delay;
  }

  /**
   * Inner class for storing host information
   */
  public class Hosts {
    private String id;
    private String host;
    private int port;

    /**
     * Gets the id of the host
     * @return the host id
     */
    public String getId() {
      return this.id;
    }

    /**
     * Gets the host
     * @return the host
     */
    public String getHost() {
      return this.host;
    }

    /**
     * Gets the port
     * @return the port
     */
    public int getPort() {
      return this.port;
    }
  }

}

