package project0.host;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ConnectionTest {

    private static final String HOST = "localhost";
    private static final int PORT = 1024;
    private Connection sender;
    private Connection receiver;

    @BeforeEach
    void setUp() {
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().
                bind(new InetSocketAddress(HOST, PORT))) {
            Future<AsynchronousSocketChannel> futureAccept = serverSocketChannel.accept();

            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            sender = new Connection(socketChannel);

            futureAccept.get();
            receiver = new Connection(futureAccept.get());
        } catch (IOException | InterruptedException | ExecutionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testSendAndReceiveShortMessage() {
        sender.send("Hello World".getBytes(StandardCharsets.UTF_8));
        byte[] message = receiver.receive();
        assertEquals(new String(message, StandardCharsets.UTF_8), "Hello World");
    }

    @Test
    void testSendAndReceiveLongMessage() {
        String text = "1 Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut at risus commodo, pharetra eros vel, " +
                "efficitur erat. Nullam dignissim mauris quis sapien ultricies, quis semper nunc porta. In condimentum " +
                "ornare tortor eget blandit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla facilisi. " +
                "Curabitur orci ante, euismod at lacus eu, pulvinar sagittis ipsum. Aliquam varius sodales dui, sit " +
                "amet porttitor neque. Maecenas sagittis finibus ligula, vitae accumsan sem dictum sit amet. Fusce " +
                "lobortis venenatis viverra. Nunc eget lobortis urna. Duis convallis egestas mauris sit amet facilisis.\n";

        sender.send(text.getBytes(StandardCharsets.UTF_8));
        byte[] message = receiver.receive();
        assertEquals(new String(message, StandardCharsets.UTF_8), text);
    }

}