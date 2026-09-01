package za.co.wethinkcode.robots.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import za.co.wethinkcode.robots.persistence.DatabaseConnection;
import za.co.wethinkcode.robots.webAPI.ApiServer;
import za.co.wethinkcode.robots.world.World;

/**
 * The Server class starts and manages the Robot World Server.
 * Supports both socket-based connections and Web API (HTTP) endpoints.
 */
public class Server {

    protected static int PORT = 5000;
    protected static int API_PORT = 8080;
    private static World world;
    static boolean running = true;
    private DatabaseConnection db;
    private static ApiServer apiServer;

    public void start(){
        db = new DatabaseConnection();
        Connection conn = db.connection();
        if(conn != null){
            System.out.println("Database connected successfully");
            db.runSchema(conn);
        }else{
            System.out.println("Failed to connect to database");
        }
    }

    public static void main(String[] args) {
        new Server().start();

        // Default values
        int port = 5000;
        int apiPort = 8080;
        int worldSize = 1;
        List<int[]> obstacles = new ArrayList<>();
        List<int[]> pits = new ArrayList<>();
        List<int[]> mines = new ArrayList<>();

        // === Parse Command Line Arguments ===
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                    port = Integer.parseInt(args[++i]);
                    break;
                case "-api":
                    apiPort = Integer.parseInt(args[++i]);
                    break;
                case "-s":
                    worldSize = Integer.parseInt(args[++i]);
                    break;
                case "-o":
                    String[] parts = args[++i].split(",");
                    if (parts.length == 2) {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        obstacles.add(new int[]{x, y});
                    }
                    break;
                case "-pt":
                    String pitArg = args[++i];
                    if (!pitArg.equalsIgnoreCase("none")) {
                        parts = pitArg.split(",");
                        if (parts.length == 2) {
                            pits.add(new int[]{
                                    Integer.parseInt(parts[0]),
                                    Integer.parseInt(parts[1])
                            });
                        }
                    }
                    break;
                case "-m":
                    String mineArg = args[++i];
                    if (!mineArg.equalsIgnoreCase("none")) {
                        parts = mineArg.split(",");
                        if (parts.length == 2) {
                            mines.add(new int[]{
                                    Integer.parseInt(parts[0]),
                                    Integer.parseInt(parts[1])
                            });
                        }
                    }
                    break;
                default:
                    System.out.println("Unknown argument: " + args[i]);
                    break;
            }
        }

        // === Configure the world ===
        try {
            if (args.length == 0) {
                world = CreateWorld.loadFrom("serverConfig.json");
                System.out.println("World configuration loaded successfully.");
            } else {
                world = CreateWorld.fromArguments(worldSize, obstacles, pits, mines);
                System.out.println("World created: size=" + worldSize +
                        " with " + obstacles.size() + " obstacles, " +
                        pits.size() + " pits, " +
                        mines.size() + " mines.");
            }

            PORT = port;
            API_PORT = apiPort;

            startServers(world);

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Starts both the Web API server and the Socket server
     */
    public static void startServers(World world) throws IOException {
        System.out.println("\n========================================");
        System.out.println("   Robot World Server Starting...");
        System.out.println("========================================");

        // Start Web API server in separate thread
        apiServer = new ApiServer(world);
        Thread apiThread = new Thread(() -> {
            apiServer.start(API_PORT);
        });
        apiThread.setDaemon(false);
        apiThread.start();

        // Small delay to let API server start
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n✓ Web API Server: http://localhost:" + API_PORT);
        System.out.println("  └─ GET http://localhost:" + API_PORT + "/world");
        System.out.println("  └─ GET http://localhost:" + API_PORT + "/world/{name}");

        System.out.println("\n✓ Socket Server: port " + PORT);
        System.out.println("  └─ Waiting for client connections...");

        System.out.println("\n========================================\n");

        // Start socket server (blocks here)
        startServer(world);
    }

    public static void startServer(World world) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Start admin thread
            Thread admin = new Thread(new ServerCommandListener(world));
            admin.setDaemon(true);
            admin.start();

            // Main loop to accept incoming client connections
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);
                    new Thread(new ClientHandler(clientSocket, world)).start();

                } catch (SocketException e) {
                    if (running) {
                        System.err.println("Socket error: " + e.getMessage());
                    } else {
                        System.out.println("Server is shutting down...");
                    }
                } catch (IOException e) {
                    System.err.println("Error with client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server socket error: " + e.getMessage());
            throw e;
        }
    }

    public static void shutdown() {
        running = false;
        if (apiServer != null) {
            apiServer.stop();
        }
        System.out.println("Server shutting down...");
    }

    public static void help() {
        System.out.println("Commands available on the Robot World Server:");
        System.out.println("help - displays available commands on the Robot World Server.");
        System.out.println("dump - displays the state of the world (robots, obstacles, etc.).");
        System.out.println("quit - disconnects all robots and ends the world.");
        System.out.println("robots - lists all robots in the world and their state.");
    }

    // Static block runs when the Server class is loaded
    // static {
    //     // Used for logging or tracking that the server has started
    //     new Recorder().logRun();
    // }
}




