package za.co.wethinkcode.robots.client.config;

/**
 * A simple configuration class used to store server connection settings.
 * This class is typically populated by deserializing a JSON configuration file.
 */
public class SimpleConfig {
    
    /** The hostname or IP address of the server. */
    public String host;

    /** The port number the client should connect to. */
    public int port;
}
