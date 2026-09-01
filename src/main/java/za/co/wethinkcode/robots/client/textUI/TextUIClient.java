//package za.co.wethinkcode.robots.client.textUI;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Scanner;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//import za.co.wethinkcode.robots.client.CommandHandler;
//import za.co.wethinkcode.robots.client.config.ConfigLoader;
//import za.co.wethinkcode.robots.client.config.SimpleConfig;
//
//public class TextUIClient {
//
//    /**
//     * Loads the client configuration from a JSON file.
//     *
//     * @return A SimpleConfig object containing configuration details like host and port.
//     *         Returns null if loading the config fails.
//     */
//    private static SimpleConfig loadConfig() {
//        try {
//            SimpleConfig config = ConfigLoader.load("clientConfig.json");
//            return config;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * Prompts the user with a message and reads a trimmed line from the console.
//     *
//     * @param console Scanner object to read user input.
//     * @param message The prompt message to display to the user.
//     * @return The trimmed input string entered by the user.
//     */
//    private static String getInput(Scanner console, String message) {
//        System.out.println(message);
//        return console.nextLine().trim();
//    }
//
//    /**
//     * Prompts the user to enter the robot's name.
//     *
//     * @param console Scanner object to read user input.
//     * @return The robot name entered by the user.
//     */
//    private static String getRobotName(Scanner console) {
//        return getInput(console, "What would you like to call your robot?");
//    }
//
//    /**
//     * Prompts the user to enter the initial shield value for the robot.
//     *
//     * @param console Scanner object to read user input.
//     * @return The initial shield value as a string.
//     */
//    private static String getRobotInitShield(Scanner console) {
//        return getInput(console, "What is the initial shield for your robot?");
//    }
//
//    /**
//     * Prompts the user to enter the initial shot count for the robot.
//     *
//     * @param console Scanner object to read user input.
//     * @return The initial shot count as a string.
//     */
//    private static String getRobotInitShot(Scanner console) {
//        return getInput(console, "What is the initial shot for your robot?");
//    }
//
//    /**
//     * Extracts the robot's state information from the server JSON response.
//     *
//     * @param response JsonObject containing the server response.
//     * @return A HashMap with keys: "position", "direction", "status", "shields", and "shots".
//     */
//    private static HashMap<String, String> extractRobotState(JsonObject response) {
//        JsonObject state = response.get("state").getAsJsonObject();
//
//        HashMap<String, String> stateAsMap = new HashMap<>();
//        stateAsMap.put("position", state.get("position").getAsString());
//        stateAsMap.put("direction", state.get("direction").getAsString());
//        stateAsMap.put("status", state.get("status").getAsString());
//        stateAsMap.put("shields", Integer.toString(state.get("shields").getAsInt()));
//        stateAsMap.put("shots", Integer.toString(state.get("shots").getAsInt()));
//
//        return stateAsMap;
//    }
//
//    /**
//     * Extracts the results of a fire command from the server JSON response.
//     *
//     * @param response JsonObject containing the server response to a fire command.
//     * @return A HashMap containing details about the impact, distance, robot hit, and shields.
//     *         If the shot missed, default values are provided.
//     */
//    private static HashMap<String, String> extractFireResults(JsonObject response) {
//        JsonObject data = response.get("data").getAsJsonObject();
//
//        HashMap<String, String> dataAsMap = new HashMap<>();
//
//        System.out.println(data);
//        String message = data.get("message").getAsString();
//        dataAsMap.put("impact", message);
//
//        if (message.equals("Miss")) {
//            // No hit data available when the shot misses
//            dataAsMap.put("distance", "0");
//            dataAsMap.put("robot", "Data unavailable");
//            dataAsMap.put("shields", "-1");
//        } else {
//            // Extract detailed hit info
//            dataAsMap.put("robot", data.get("robot").getAsString());
//            dataAsMap.put("distance", data.get("distance").getAsString());
//            dataAsMap.put("shots", response.get("state").getAsJsonObject().get("shots").getAsString());
//
//            JsonObject state = data.getAsJsonObject("state");
//            if (state.has("shields")) {
//                dataAsMap.put("shields", state.get("shields").getAsString());
//            } else {
//                dataAsMap.put("shields", "-1");
//            }
//        }
//
//        return dataAsMap;
//    }
//
//    /**
//     * Extracts the data from a look command response, organizing visible objects by direction.
//     *
//     * @param response JsonObject containing the server response to a look command.
//     * @return A list of four lists, each containing strings describing objects in the directions:
//     *         North, East, South, and West.
//     */
//    private static List<List<String>> extractLookData(JsonObject response) {
//        JsonObject data = response.get("data").getAsJsonObject();
//        JsonArray objects = data.get("objects").getAsJsonArray();
//
//        List<String> north = new ArrayList<>();
//        List<String> east = new ArrayList<>();
//        List<String> south = new ArrayList<>();
//        List<String> west = new ArrayList<>();
//
//        for (JsonElement object : objects) {
//            JsonObject o = object.getAsJsonObject();
//
//            String direction = o.get("direction").getAsString();
//            String type = o.get("type").getAsString();
//            String distance = o.get("distance").getAsString();
//
//            // Format the description of each object by direction
//            switch (direction) {
//                case "NORTH":
//                    north.add(MessageFormat.format("{0} ({1} steps away)", type, distance));
//                    break;
//                case "EAST":
//                    east.add(MessageFormat.format("{0} ({1} steps away)", type, distance));
//                    break;
//                case "SOUTH":
//                    south.add(MessageFormat.format("{0} ({1} steps away)", type, distance));
//                    break;
//                case "WEST":
//                    west.add(MessageFormat.format("{0} ({1} steps away)", type, distance));
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        return List.of(north, east, south, west);
//    }
//
//    /**
//     * The main method initializes the client, handles user input,
//     * communicates with the Robot World server, and displays the game state.
//     *
//     * @param args Command line arguments (not used).
//     * @throws IOException if there are issues with networking or IO.
//     */
//    public static void main(String[] args) throws IOException {
//        Scanner console = new Scanner(System.in);
//        Socket socket = null;
//
//        // Prompt user for robot details
//        final String robotName = getRobotName(console);
//        final String maxShield = getRobotInitShield(console);
//        final String maxShot = getRobotInitShot(console);
//        AtomicInteger prevCommand = new AtomicInteger(-1);  // Tracks last command sent
//
//        try {
//            // Load server configuration and connect
//            SimpleConfig config = loadConfig();
//            socket = new Socket(config.host, config.port);
//
//            System.out.println("Connected to Robot World server");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//
//            // Start a listener thread to receive and process server messages asynchronously
//            new Thread(() -> {
//                try {
//                    String response;
//                    JsonObject prevJsonObject = null;
//                    while ((response = in.readLine()) != null) {
//                        // Use previous JSON object if the last command was a fire command
//                        JsonObject jsonRes = prevCommand.get() == 2
//                            ? prevJsonObject
//                            : JsonParser.parseString(response).getAsJsonObject();
//                        String results = jsonRes.get("result").getAsString();
//
//                        if (results.equals("ERROR")) {
//                            System.out.println("ERROR: " + jsonRes.get("message"));
//                            continue;
//                        }
//
//                        else if (jsonRes.has("message") && jsonRes.get("message").getAsString().equals("DEAD")) {
//                            System.out.println("You are dead. Game over!!");
//                            System.exit(1);
//                        }
//
//                        // Extract and display robot state information
//                        HashMap<String, String> state = extractRobotState(jsonRes);
//
//                        List<List<String>> lookList = null;
//                        if (prevCommand.get() == 1) lookList = extractLookData(jsonRes);
//
//                        JsonObject fireObj = null;
//                        HashMap<String, String> fire = null;
//                        if (prevCommand.get() == 2) {
//                            fireObj = JsonParser.parseString(response).getAsJsonObject();
//                            fire = extractFireResults(fireObj);
//                        } else {
//                            prevJsonObject = jsonRes;
//                        }
//
//                        // Determine shots left depending on the last command's result
//                        int shots = prevCommand.get() == 2 && fire.get("impact").equals("Hit")
//                            ? Integer.parseInt(fire.get("shots"))
//                            : Integer.parseInt(state.get("shots"));
//
//                        // Create and draw the UI with updated info
//                        TextUserInterface textUI = new TextUserInterface(
//                            robotName,
//                            Integer.parseInt(state.get("shields")),
//                            shots,
//                            state.get("direction"),
//                            state.get("position"),
//                            state.get("status"),
//                            lookList == null ? List.of(List.of(), List.of(), List.of(), List.of()) : lookList
//                        );
//
//                        if (prevCommand.get() == 2) {
//                            textUI.setImpact(fire.get("impact"));
//                            textUI.setHitRobotDistance(Integer.parseInt(fire.get("distance")));
//                            textUI.setHitRobotShield(Integer.parseInt(fire.get("shields")));
//                            textUI.setHitRobotName(fire.get("robot"));
//                        }
//
//                        textUI.draw();
//                    }
//                }
//                catch (IOException e) {
//                    System.out.println("Disconnected from the server");
//                }
//            }).start();
//
//            // Launch the robot with the initial settings provided by the user
//            String launchCommand = MessageFormat.format("{0} launch {1} {2}", robotName, maxShield, maxShot);
//            boolean isCommandValid = CommandHandler.validateUserInput(launchCommand);
//            if (!isCommandValid) {
//                System.out.println("Couldn't launch robot. Client aborted!");
//                System.exit(1);
//            }
//
//            JsonObject req = CommandHandler.handle(launchCommand);
//            out.println(req);
//
//            // Main input loop for user commands
//            String userInput;
//            while ((userInput = console.nextLine()) != null) {
//                if (userInput.contains("launch")) {
//                    System.out.println("You cannot launch more than 1 robot with the TextUI Client. Sorry :(");
//                    continue;
//                }
//
//                userInput = robotName + " " + userInput;
//
//                boolean isValid = CommandHandler.validateUserInput(userInput);
//                if (!isValid) continue;
//
//                JsonObject request = CommandHandler.handle(userInput);
//                if (request == null) continue;
//
//                out.println(request);
//
//                String command = request.get("command").getAsString();
//                if (command.equals("look")) prevCommand.set(1);
//                else if (command.equals("fire")) prevCommand.set(2);
//                else prevCommand.set(0);
//            }
//        }
//        catch (IOException e) {
//            System.err.println(e.getMessage());
//            System.exit(0);
//        }
//
//        assert socket != null;
//        socket.close();
//        console.close();
//        System.exit(0);
//    }
//}
