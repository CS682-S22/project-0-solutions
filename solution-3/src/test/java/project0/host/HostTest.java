package project0.host;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import project0.Config;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class HostTest {

    private static final Gson GSON = new Gson();
    private static final String HOST_1_DEFAULT = "src/test/test_configs/host1default.json";
    private static final String HOST_2_DEFAULT = "src/test/test_configs/host2default.json";
    private static final String HOST_3_DEFAULT = "src/test/test_configs/host3default.json";
    private static final String HOST_1_LOSSY = "src/test/test_configs/host1lossy.json";
    private static final String HOST_2_LOSSY = "src/test/test_configs/host2lossy.json";
    private static final String HOST_3_LOSSY = "src/test/test_configs/host3lossy.json";
    private static final String HOST_1_FULL_LOSSY = "src/test/test_configs/host1fulllossy.json";
    private static final String HOST_2_FULL_LOSSY = "src/test/test_configs/host2fulllossy.json";
    private static final String HOST_3_FULL_LOSSY = "src/test/test_configs/host3fulllossy.json";
    private Host host1;
    private Host host2;
    private Host host3;

    @AfterEach
    void tearDown() {
        host1.close();
        host2.close();
        host3.close();
    }

    @Test
    void testHostWithDefaultConnection() {
        try {
            Config config1 = GSON.fromJson(new FileReader(HOST_1_DEFAULT), Config.class);
            Config config2 = GSON.fromJson(new FileReader(HOST_2_DEFAULT), Config.class);
            Config config3 = GSON.fromJson(new FileReader(HOST_3_DEFAULT), Config.class);
            host1 = new Host(config1);
            host2 = new Host(config2);
            host3 = new Host(config3);
            host3.startServer();
            host2.startServer();
            host2.startClient();
            host1.startClient();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
            assertEquals(lineCount(config1.getOutFile()), 80);
            assertEquals(lineCount(config2.getOutFile()), 60);
            assertEquals(lineCount(config3.getOutFile()), 40);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHostWithLossyConnection() {
        try {
            Config config1 = GSON.fromJson(new FileReader(HOST_1_LOSSY), Config.class);
            Config config2 = GSON.fromJson(new FileReader(HOST_2_LOSSY), Config.class);
            Config config3 = GSON.fromJson(new FileReader(HOST_3_LOSSY), Config.class);
            host1 = new Host(config1);
            host2 = new Host(config2);
            host3 = new Host(config3);
            host3.startServer();
            host2.startServer();
            host2.startClient();
            host1.startClient();
            try {
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
            assertTrue(lineCount(config1.getOutFile()) < 80);
            assertTrue(lineCount(config2.getOutFile()) < 60);
            assertTrue(lineCount(config3.getOutFile()) < 40);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHostWithFullLoss() {
        try {
            Config config1 = GSON.fromJson(new FileReader(HOST_1_FULL_LOSSY), Config.class);
            Config config2 = GSON.fromJson(new FileReader(HOST_2_FULL_LOSSY), Config.class);
            Config config3 = GSON.fromJson(new FileReader(HOST_3_FULL_LOSSY), Config.class);
            host1 = new Host(config1);
            host2 = new Host(config2);
            host3 = new Host(config3);
            host3.startServer();
            host2.startServer();
            host2.startClient();
            host1.startClient();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
            assertFalse(new File(config1.getOutFile()).exists());
            assertFalse(new File(config2.getOutFile()).exists());
            assertFalse(new File(config3.getOutFile()).exists());
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    private int lineCount(String path) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return count;
    }
}