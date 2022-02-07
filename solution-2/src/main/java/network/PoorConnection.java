/**
 * This class is a for making poor connections
 *
 */
package network;

import java.net.Socket;
import models.Config;
import utils.RandomNumbers;
import utils.Utils;

/**
 * The poor connection class is used to simulate poor connections
 */
public class PoorConnection extends Connection {
  RandomNumbers randomNumbers;

  public PoorConnection(Config config, Socket socket) {
    super(config, socket);
    this.randomNumbers = new RandomNumbers();
  }

  @Override
  public byte[] receive() {
    if (this.config.isDelay()) {
      Utils.sleep(config.getDelayRate());
    }
    if (this.config.isLossy()) {
      boolean success = this.randomNumbers.controlledFailure(this.config.getLossRate());
      if (!success) {
        return new byte[0];
      }
    }
    return super.receive();
  }

  @Override
  public boolean send(byte[] message) {
    if (this.config.isDelay()) {
      Utils.sleep(this.config.getDelayRate());
    }
    if (this.config.isLossy()) {
      boolean success = this.randomNumbers.controlledFailure(this.config.getLossRate());
      if (!success) {
        return false;
      }
    }
    return super.send(message);
  }
}
