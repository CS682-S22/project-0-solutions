/**
 * This is a File Utility class for static file processing functions
 *
 * This is project3 from cs601.
 *
 */
package utils;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Class for file utils
 */
public class FileUtils {

  /**
   *
   * This was modified to be generic
   *
   * Reviewed GSON source code to understand how to use a generic class and generic return type
   * https://github.com/google/gson/blob/ceae88bd6667f4263bbe02e6b3710b8a683906a2/gson/src/main/java/com/google/gson/Gson.java#L816
   *
   * Parses the any file
   *
   * @param gson gson object used for deserializing the config json
   * @param filename
   * @param classOfT
   * @param <T>
   * @return the File as an object
   */
  public static <T> T parseFile(Gson gson, String filename, Class<T> classOfT) {
    StringBuilder lines = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
      reader.lines().forEach(lines::append);
    } catch (IOException e) {
      System.out.printf("Could not find required file %s exiting now\n", filename);
      System.exit(1);
    }
    return gson.fromJson(lines.toString(), classOfT);
  }

}
