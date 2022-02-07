import configurations.Config;
import configurations.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * Responsible for testing functions in class NodeApplication
 *
 */
public class NodeApplicationTest {
    private static NodeApplication nodeApplication;

    @BeforeAll
    public static void init() {
        nodeApplication = new NodeApplication();
    }

    /**
     * Tests if verifyConfig() function return false if source object is null
     */
    @Test
    public void verifyConfig_noSource_returnFalse() {
        Config configuration = new Config();
        configuration.setLossRate(0);

        setConfiguration(configuration);

        Assertions.assertFalse(nodeApplication.verifyConfig());
    }

    /**
     * Tests if verifyConfig() function return false if loss rate is not between 0 and 1
     */
    @Test
    public void verifyConfig_inCorrectLossRate_returnFalse() {
        Config configuration = new Config();
        configuration.setLossRate(5);
        configuration.setSource(new Source("localhost", 8080));

        setConfiguration(configuration);

        Assertions.assertFalse(nodeApplication.verifyConfig());
    }

    /**
     * Tests if verifyConfig() function return true if the config contains all the required valid input
     */
    @Test
    public void verifyConfig_validData_returnTrue() {
        Config configuration = new Config();
        configuration.setSource(new Source("localhost", 8080));

        setConfiguration(configuration);

        Assertions.assertTrue(nodeApplication.verifyConfig());
    }

    private static void setConfiguration(Config configuration) {
        try {
            Field config = NodeApplication.class.getDeclaredField("configuration");
            config.setAccessible(true);
            config.set(nodeApplication, configuration);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }
}
