package za.co.wethinkcode.robots.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import za.co.wethinkcode.robots.client.custom_types.Argument;
import za.co.wethinkcode.robots.client.custom_types.IntArgument;
import za.co.wethinkcode.robots.client.custom_types.StringArgument;

/**
 * Builds JsonObject requests to send commands to the Robot World server.
 */
public class Request {

    /**
     * Creates a JsonObject message representing a command for the robot.
     *
     * @param name      the name of the robot
     * @param command   the command to execute
     * @param arguments the list of arguments for the command
     * @return JsonObject representing the command message
     */
    private static JsonObject message(String name, String command, List<Argument> arguments) {
        JsonObject message = new JsonObject();
        message.addProperty("robot", name);
        message.addProperty("command", command);

        JsonArray args = new JsonArray();
        for (Argument arg : arguments) {
            if (arg instanceof IntArgument intArg) {
                args.add(intArg.value());
            } else if (arg instanceof StringArgument strArg) {
                args.add(strArg.value());
            }
        }

        message.add("arguments", args);
        return message;
    }

    /**
     * Creates a launch command request with robot name, max shield, and max shots.
     *
     * @param robotName the name of the robot
     * @param maxShield the max shield value
     * @param maxShots  the max shots value
     * @return JsonObject representing the launch command
     */
    public static JsonObject launch(String robotName, int maxShield, int maxShots) {
        List<Argument> args = new ArrayList<>();
        args.add(new StringArgument("Normal"));
        args.add(new IntArgument(maxShield));
        args.add(new IntArgument(maxShots));

        return message(robotName, "launch", args);
    }

    /**
     * Creates a state command request to check the robot's current state.
     *
     * @param robotName the name of the robot
     * @return JsonObject representing the state command
     */
    public static JsonObject state(String robotName) {
        return message(robotName, "state", new ArrayList<>());
    }

    /**
     * Creates a look command request to get the surroundings of the robot.
     *
     * @param robotName the name of the robot
     * @return JsonObject representing the look command
     */
    public static JsonObject look(String robotName) {
        return message(robotName, "look", new ArrayList<>());
    }

    /**
     * Creates a forward movement command request.
     *
     * @param robotName the name of the robot
     * @param steps     number of steps to move forward
     * @return JsonObject representing the forward command
     */
    public static JsonObject forward(String robotName, int steps) {
        List<Argument> args = new ArrayList<>();
        args.add(new IntArgument(steps));

        return message(robotName, "forward", args);
    }

    /**
     * Creates a backward movement command request.
     *
     * @param robotName the name of the robot
     * @param steps     number of steps to move backward
     * @return JsonObject representing the back command
     */
    public static JsonObject backward(String robotName, int steps) {
        List<Argument> args = new ArrayList<>();
        args.add(new IntArgument(steps));

        return message(robotName, "back", args);
    }

    /**
     * Creates a turn left command request.
     *
     * @param robotName the name of the robot
     * @return JsonObject representing the turn left command
     */
    public static JsonObject turnLeft(String robotName) {
        List<Argument> args = new ArrayList<>();
        args.add(new StringArgument("left"));

        return message(robotName, "turn", args);
    }

    /**
     * Creates a turn right command request.
     *
     * @param robotName the name of the robot
     * @return JsonObject representing the turn right command
     */
    public static JsonObject turnRight(String robotName) {
        List<Argument> args = new ArrayList<>();
        args.add(new StringArgument("right"));

        return message(robotName, "turn", args);
    }

    /**
     * Creates a repair command request.
     *
     * @param robotName the name of the robot
     * @return JsonObject representing the repair command
     */
    public static JsonObject repair(String robotName) {
        return message(robotName, "repair", new ArrayList<>());
    }

    /**
     * Creates a reload command request.
     *
     * @param robotName the name of the robot
     * @return JsonObject representing the reload command
     */
    public static JsonObject reload(String robotName) {
        return message(robotName, "reload", new ArrayList<>());
    }

    /**
     * Creates a fire command request.
     *
     * @param robotName the name of the robot
     * @return JsonObject representing the fire command
     */
    public static JsonObject fire(String robotName) {
        return message(robotName, "fire", new ArrayList<>());
    }
}
