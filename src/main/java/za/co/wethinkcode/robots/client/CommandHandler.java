package za.co.wethinkcode.robots.client;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import za.co.wethinkcode.robots.utilities.FuzzySearch;

/**
 * Handles parsing, validating, and processing user commands
 * for the robot client application.
 */
public class CommandHandler {

    /**
     * Checks if the given input string can be parsed as an integer.
     *
     * @param input the string to check
     * @return true if input is a valid integer, false otherwise
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Prints the help information including usage and available commands.
     */
    public static void printHelp() {
        printUsage();
        System.out.println();
    }

    /**
     * Prints usage instructions and lists available commands.
     */
    public static void printUsage() {
        System.out.println(
            """

            Project Description: ...
            
            Usage: <robot's name> <command> *<args...>

            Example: josh forward 5

            """
        );

        List<String> commands = getAvailaibleCommands();
        System.out.println("Try one of these commands: " + commands.toString() + "\n");
    }

    /**
     * Returns a list of available commands for the robot client.
     *
     * @return list of command strings
     */
    private static List<String> getAvailaibleCommands() {
        List<String> commands = new ArrayList<>();
        commands.add("launch");
        commands.add("state");
        commands.add("look");
        commands.add("left");
        commands.add("right");
        commands.add("repair");
        commands.add("reload");
        commands.add("fire");
        commands.add("forward");
        commands.add("back");
        commands.add("help");
        return commands;
    }

    /**
     * Prints suggestions for commands similar to the given invalid command.
     *
     * @param command the invalid command input by the user
     */
    public static void printAvailableCommands(String command) {
        List<String> commands = getAvailaibleCommands();

        List<String> similarCommands = FuzzySearch.find(commands, command, 50.0);

        if (similarCommands.size() == 0) {
            System.out.println(MessageFormat.format("\ncommand {0} is not found and there are no similar commands\n", 
            command));
        } else {
            System.out.println(MessageFormat.format("\ncommand {0} is not found. Did you mean to try: {1}\n", 
            command, similarCommands.toString()));
        }
    }

    /**
     * Validates the user input command string.
     * Checks command format, argument counts, and argument types.
     * Exits program if user inputs "disconnect" or "quit".
     *
     * @param userInput the full input command string from the user
     * @return true if input is valid, false otherwise
     */
    public static boolean validateUserInput(String userInput) {
        String[] args = userInput.split(" ");

        String firstArg = args[0].toLowerCase();
        if (args.length == 1 
            && (firstArg.equals("disconnect") 
            || firstArg.equals("quit"))) {
                System.out.println("Disconnected from server");
                System.exit(0);
            };

        if (args.length < 2) {
            printUsage();
            return false;
        }

        String command = args[1].toLowerCase();
        switch (command) {
            case "launch":
                if (args.length != 4) {
                    printUsage();
                    return false;
                };

                if (!isInteger(args[2]) || !isInteger(args[3])) {
                    System.out.println("Arguments for launch should be numbers");
                    return false;
                }
                break;

            case "state":
            case "look":
            case "left":
            case "right":
            case "repair":
            case "reload":
            case "fire":
                if (args.length != 2) {
                    printUsage();
                    return false;
                }
                break;

            case "forward":
            case "back":
                if (args.length != 3) {
                    printUsage();
                    return false;
                }

                if (!isInteger(args[2])) {
                    System.out.println("Arguments for forward/back should be numbers");
                    return false;
                }
                break;

            case "help":
                printHelp();
                break;

            default:
                printAvailableCommands(command);
                return false;
        }

        return true;
    }

    /**
     * Processes a validated user input command and returns
     * the corresponding JsonObject request to send to the server.
     *
     * @param userInput the full input command string from the user
     * @return JsonObject representing the request, or null if command is invalid
     */
    public static JsonObject handle(String userInput) {
        String[] args = userInput.toLowerCase().split(" ");

        String robotName = args[0];
        String command = args[1];
        switch (command) {
            case "launch":
                return Request.launch(robotName, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        
            case "state":
                return Request.state(robotName);

            case "look":
                return Request.look(robotName);

            case "forward":
                return Request.forward(robotName, Integer.parseInt(args[2]));

            case "back":
                return Request.backward(robotName, Integer.parseInt(args[2]));

            case "left":
                return Request.turnLeft(robotName);

            case "right":
                return Request.turnRight(robotName);

            case "repair":
                return Request.repair(robotName);

            case "reload":
                return Request.reload(robotName);

            case "fire":
                return Request.fire(robotName);

            default:
                return null;
        }
    }
}
