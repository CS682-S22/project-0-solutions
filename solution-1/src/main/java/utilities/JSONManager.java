package utilities;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;

/**
 * Responsible for serializing JSON string to object
 *
 */
public class JSONManager {
    /**
     * Parse JSON string into an object
     * @param reader Reader object
     * @param classOfT Type of Class
     * @return Parsed object
     */
    public static <T> T fromJson(Reader reader, Class<T> classOfT) {
        Gson gson = new Gson();

        T object = null;

        try {
            object = gson.fromJson(reader, classOfT);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to parse json");
        }

        return object;
    }
}
