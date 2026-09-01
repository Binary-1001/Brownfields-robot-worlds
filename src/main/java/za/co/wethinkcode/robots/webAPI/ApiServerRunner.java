package za.co.wethinkcode.robots.webAPI;

import za.co.wethinkcode.robots.world.World;
import za.co.wethinkcode.robots.server.CreateWorld;
import za.co.wethinkcode.robots.persistence.DatabaseConnection;

import java.sql.Connection;

/**
 * Standalone runner for the Web API Server.
 * This can run independently or alongside the socket server.
 */
public class ApiServerRunner {

    private static final int DEFAULT_API_PORT = 8080;
    private DatabaseConnection db;

    public void initializeDatabase() {
        db = new DatabaseConnection();
        Connection conn = db.connection();
        if(conn != null){
            System.out.println("Database connected successfully");
            db.runSchema(conn);
        } else {
            System.out.println("Failed to connect to database");
        }
    }

    public static void main(String[] args) {
        ApiServerRunner runner = new ApiServerRunner();
        runner.initializeDatabase();

        int apiPort = DEFAULT_API_PORT;
        int worldSize = 10;

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-api":
                case "-p":
                    apiPort = Integer.parseInt(args[++i]);
                    break;
                case "-s":
                    worldSize = Integer.parseInt(args[++i]);
                    break;
                default:
                    break;
            }
        }

        // Create a world instance for the API
        World world;
        if (args.length == 0) {
            // Try to load from config file
            try {
                world = CreateWorld.loadFrom("serverConfig.json");
                System.out.println("World loaded from config file.");
            } catch (Exception e) {
                // Fallback to default world
                world = new World(worldSize);
                System.out.println("Created default world: size=" + worldSize);
            }
        } else {
            world = new World(worldSize);
            System.out.println("Created world: size=" + worldSize);
        }

        // Start the API server
        ApiServer apiServer = new ApiServer(world);
        apiServer.start(apiPort);

        System.out.println("\nWeb API Server is running!");
        System.out.println("Available endpoints:");
        System.out.println("  GET http://localhost:" + apiPort + "/world");
        System.out.println("  GET http://localhost:" + apiPort + "/world/{worldName}");
        System.out.println("\nPress Ctrl+C to stop the server.");

    }
}