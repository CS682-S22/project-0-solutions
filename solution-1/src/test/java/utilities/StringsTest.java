package utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Responsible for testing the functions in class Strings
 *
 */
public class StringsTest {
    /**
     * Tests if the isNullOrEmpty() function return true if the string is null
     */
    @Test
    public void isNullOrEmpty_nullValue_returnTrue() {
        String value = null;

        Assertions.assertTrue(Strings.isNullOrEmpty(value));
    }

    /**
     * Tests if the isNullOrEmpty() function return true if the string is empty
     */
    @Test
    public void isNullOrEmpty_emptyValue_returnTrue() {
        String value = "";

        Assertions.assertTrue(Strings.isNullOrEmpty(value));
    }

    /**
     * Tests if the isNullOrEmpty() function return true if the string contains only whitespace
     */
    @Test
    public void isNullOrEmpty_whitespaceOnly_returnTrue() {
        String value = " ";

        Assertions.assertTrue(Strings.isNullOrEmpty(value));
    }

    /**
     * Tests if the isNullOrEmpty() function return false if the string contains value
     */
    @Test
    public void isNullOrEmpty_nonEmptyString_returnFalse() {
        String value = "testing";

        Assertions.assertFalse(Strings.isNullOrEmpty(value));
    }
}
