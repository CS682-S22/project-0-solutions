package controllers;

import utilities.Util;

import java.net.Socket;

/**
 * Responsible for handling connections between hosts when there can be a possibility of data being lost or delayed
 *
 */
public class LossyConnection extends Connection {

    private int noOfMsgSend = 1;
    private int noOfMsgToSend;
    private int delayInMS;

    public LossyConnection(Socket channel, int msgCount, float lossRate, int delayInMS, String destinationIPAddress, int destinationPort, String sourceIPAddress, int sourcePort) {
        super(channel, msgCount, destinationIPAddress, destinationPort, sourceIPAddress, sourcePort);
        if(lossRate > 0) {
            if (msgCount < 10) noOfMsgToSend = msgCount - 1;
            else noOfMsgToSend = msgCount - (int) Math.ceil(msgCount * lossRate);
        } else {
            noOfMsgToSend = msgCount + 1; // one for "exit" identifier
        }
        this.delayInMS = delayInMS;
    }

    /**
     * Sends the message to another host.
     *
     * Will sleep for the mentioned time.
     * Will send the mentioned number of messages to another host in order to loss few messages
     * @param message message to send to another host
     * @return true if successful or false
     */
    @Override
    public boolean send(byte[] message) {
        boolean isSend = false;

        if(noOfMsgSend <= noOfMsgToSend) {
            if(delayInMS != 0) {
                System.out.printf("[%s:%d] Sleeping for %d time for delaying the message.\n", sourceIPAddress, sourcePort, delayInMS);
                Util.sleep(delayInMS);
            }
            isSend = super.send(message);
            if(isSend) noOfMsgSend++;
        }

        return isSend;
    }
}
