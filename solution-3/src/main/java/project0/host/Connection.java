package project0.host;

import com.google.protobuf.ByteString;
import project0.protos.Content;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class to establish a connection to send and receive messages over socket.
 *
 * <p>
 * Reference: https://seb-nyberg.medium.com/length-delimited-protobuf-streams-a39ebc4a4565
 * Reference: https://medium.com/@dmi3coder/c-java-socket-communication-easily-lets-try-protobuf-586b18521f79
 */
public class Connection implements Receiver, Sender {
    /**
     * the socket channel.
     */
    private final AsynchronousSocketChannel socketChannel;
    /**
     * the message queue for receiving message.
     */
    private final Queue<byte[]> messages;
    /**
     * the buffer to read message.
     */
    private final ByteBuffer buffer;
    /**
     * the future object to read buffer.
     */
    private Future<Integer> readResult;

    /**
     * Constructor.
     *
     * @param socketChannel the socket channel
     */
    public Connection(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.messages = new LinkedList<>();
        this.buffer = ByteBuffer.allocate(1024);
        this.readResult = null;
    }

    /**
     * Method to read message from remote host.
     *
     * @return byte array of the message
     */
    @Override
    public byte[] receive() {
        if (!messages.isEmpty()) {
            return messages.poll();
        }
        if (socketChannel != null && socketChannel.isOpen()) {
            if (readResult == null) {
                readResult = socketChannel.read(buffer);
            }
            try {
                if (readResult.get(500, TimeUnit.MILLISECONDS) != -1 && readResult.isDone()) {
                    readResult = null;
                    int size = buffer.position();
                    int count = 0;
                    buffer.flip(); // set the position to 0
                    while (count + 4 < size) { // need 4 bytes to read an int
                        int length = buffer.getInt();
                        if (count + length > size) { // message is partially read in due to buffer capacity
                            break;
                        }
                        byte[] bytes = new byte[length];
                        buffer.get(bytes, 0, length);
                        Content.Body contentBody = Content.Body.parseFrom(bytes); // decode byte array into protobuf
                        messages.add(contentBody.getText().toByteArray());
                        count += (length + 4);
                    }
                    if (count < size) { // bytes not read due to buffer capacity
                        buffer.position(count);
                        byte[] bytes = new byte[size - count];
                        buffer.get(bytes, 0, bytes.length);
                        buffer.clear();
                        buffer.put(bytes, 0, bytes.length);
                    } else {
                        buffer.clear();
                    }
                }
            } catch (IOException | InterruptedException | ExecutionException e) {
                System.err.println("receive(): " + e.getMessage());
            } catch (TimeoutException e) {
                // do nothing
            }
        }
        return messages.poll();
    }

    /**
     * Method to send byte array over the socket.
     *
     * @param message byte array
     * @return true if message is sent, else false
     */
    @Override
    public boolean send(byte[] message) {
        try {
            Content.Body contentBody = Content.Body.newBuilder().setText(ByteString.copyFrom(message)).build(); //encode byte array into protobuf
            ByteBuffer buffer = ByteBuffer.allocate(contentBody.toByteArray().length + 4);
            buffer.putInt(contentBody.toByteArray().length);
            buffer.put(contentBody.toByteArray());
            buffer.flip();
            Future<Integer> writeResult = socketChannel.write(buffer);
            writeResult.get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("send(byte[] message): " + e.getMessage());
            return false;
        }
    }
}
