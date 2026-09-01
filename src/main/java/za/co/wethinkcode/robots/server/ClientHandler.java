package za.co.wethinkcode.robots.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import za.co.wethinkcode.robots.command.Command;
import za.co.wethinkcode.robots.world.World;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles communication between a connected client and the Robot World server.
 * Processes input commands, validates robot access, and executes commands through the Command API.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final World world;

    private List<String> launchedRobots = new ArrayList<>();

    /**
     * Constructs a new ClientHandler for a given client socket and shared world instance.
     *
     * @param socket the client connection socket
     * @param world the world to apply commands to
     */
    public ClientHandler(Socket socket, World world) {
        this.socket = socket;
        this.world = world;
    }

    /**
     * Checks if the received command is a launch command.
     *
     * @param request the parsed client JSON request
     * @return true if the command is "launch", otherwise false
     */
    private boolean launchingRobot(JsonObject request) {
        String command = request.get("command").getAsString();
        return command.equals("launch");
    }

    /**
     * Retrieves the robot's name from a request.
     *
     * @param request the JSON request containing the robot name
     * @return the robot's name string
     */
    private String getRobotName(JsonObject request) {
        return request.get("robot").getAsString();
    }

    /**
     * Checks if a robot exists in the world.
     *
     * @param world the world instance
     * @param robotName the robot name to check
     * @return true if robot exists, otherwise false
     */
    private boolean robotExistsInWorld(World world, String robotName) {
        return world.getRobotWithName(robotName) != null;
    }

    /**
     * Creates a response for unlaunched robot command attempts.
     *
     * @return a standardized JSON error response
     */
    private JsonObject getRobotError() {
        JsonObject response = new JsonObject();
        response.addProperty("result", "ERROR");

        JsonObject data = new JsonObject();
        data.addProperty("message", "robot does not exist");
        response.add("data",data);
        return response;
    }

    /**
     * Returns a response when a dead robot receives a command.
     *
     * @return a simple OK message indicating the robot is dead
     */
    private JsonObject getDeadRobotStatus() {
        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");
        response.addProperty("message", "DEAD");
        return response;
    }

    /**
     * Continuously reads from client socket, processes input as commands,
     * validates robot launch and ownership, then dispatches valid commands.
     */
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String input;
            while ((input = in.readLine()) != null) {
                JsonObject request = JsonParser.parseString(input).getAsJsonObject();

                String robotName = this.getRobotName(request);
                if (this.world.isRobotDead(robotName)) {
                    out.println(this.getDeadRobotStatus());
                    continue;
                }

                else if (this.launchingRobot(request) && !this.robotExistsInWorld(world, robotName)) {
                    launchedRobots.add(robotName);
                }

                else if (!this.launchingRobot(request) && !this.launchedRobots.contains(robotName)) {
                    out.println(this.getRobotError());
                    continue;
                }

                JsonObject response = Command.manage(world, request);
                out.println(response.toString());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        }
    }
}
