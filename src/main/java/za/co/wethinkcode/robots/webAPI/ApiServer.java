package za.co.wethinkcode.robots.webAPI;

import io.javalin.Javalin;
import za.co.wethinkcode.robots.world.World;

/**
 * ApiServer sets up and runs the Robot Worlds Web API using Javalin.
 * Handles HTTP endpoints for world and robot operations.
 */
public class ApiServer {
    private final Javalin server;
    private World world;

    /**
     * Constructs an ApiServer with the given World instance.
     * Sets up HTTP endpoints for world operations.
     *
     * @param world the World instance to serve via API
     */
    public ApiServer(World world) {
        this.world = world;
        server = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        });

        // GET /world - returns current world state
        server.get("/world", context -> ApiHandler.getCurrentWorld(context, this.world));

        // GET /world/{world} - retrieves world by name from database
        server.get("/world/{world}", context -> ApiHandler.getWorld(context, this.world));
    }

    /**
     * Starts the API server on the specified port.
     *
     * @param port the port number to listen on
     */
    public void start(int port) {
        this.server.start(port);
        // Message will be printed by caller
    }

    /**
     * Stops the API server.
     */
    public void stop() {
        this.server.stop();
    }
}