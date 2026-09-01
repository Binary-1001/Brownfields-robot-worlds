package za.co.wethinkcode.robots.client.config;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class responsible for loading configuration settings from a JSON file.
 */
public class ConfigLoader {

    /**
     * Loads a configuration file from the specified file path and parses it into a {@link SimpleConfig} object.
     *
     * @param path the file path to the JSON configuration file
     * @return a {@link SimpleConfig} instance containing the loaded configuration
     * @throws IOException if the file cannot be read
     */
    public static SimpleConfig load(String path) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, SimpleConfig.class);
        }
    }
}
