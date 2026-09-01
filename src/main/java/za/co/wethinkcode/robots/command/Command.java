package za.co.wethinkcode.robots.command;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.world.World;

/**
 * Abstract base class for all robot commands.
 * Each command is associated with a world and a JSON request object.
 * Subclasses should implement the {@link #execute()} method to define command-specific behavior.
 */
public abstract class Command {
    private final List<Object> arguments = new ArrayList<>();

    protected World world;
    protected JsonObject jsonObject;

    /**
     * Constructs a Command with the given world context and JSON request object.
     *
     * @param world the game world context in which the command will be executed
     * @param jsonObject the JSON object representing the incoming command request
     */
    public Command(World world, JsonObject jsonObject) {
        this.world = world;
        this.jsonObject = jsonObject;
    }

    // Adds an argument to the command's internal list
    protected void addArgument(Object arg) {
        this.arguments.add(arg);
    }

    // Returns the list of arguments associated with the command
    protected List<Object> getArguments() {
        return this.arguments;
    }

    /**
     * Executes the command.
     *
     * @return a JsonObject representing the response to this command
     * @throws InterruptedException if execution is interrupted
     */
    public abstract JsonObject execute() throws InterruptedException;

    // Helper method to parse and assign arguments from a JsonArray to a command
    protected static void setArguments(Command command, JsonArray arguments) {
        for (JsonElement arg : arguments) {
            JsonPrimitive primitive = arg.getAsJsonPrimitive();
            if (primitive.isString()) {
                String value = primitive.getAsString();
                command.addArgument(value);
            } else if (primitive.isNumber()) {
                Number value = primitive.getAsNumber();
                command.addArgument(value);
            }
        }
    }

    /**
     * Dispatches the command based on the "command" field in the input JSON.
     * Instantiates the appropriate Command subclass, sets its arguments,
     * and executes it.
     *
     * @param world the game world in which the command should operate
     * @param jsonObj the full JSON request from the client
     * @return a JsonObject representing the result of the command execution
     * @throws InterruptedException if execution is interrupted
     */
    public static JsonObject manage(World world, JsonObject jsonObj) throws InterruptedException {
        String command = jsonObj.get("command").getAsString();

        ProtocolResponse protocol = new ProtocolResponse();

        JsonArray arguments = jsonObj.get("arguments").getAsJsonArray();

        return switch (command) {
            case "launch" -> {
                Command launchCommand = new LaunchCommand(world, jsonObj);
                setArguments(launchCommand, arguments);
                yield launchCommand.execute();
            }
            case "look" -> {
                Command lookCommand = new LookCommand(world, jsonObj);
                setArguments(lookCommand, arguments);
                yield lookCommand.execute();
            }
            case "save" -> {
                Command saveCommand = new SaveCommand(world, jsonObj);
                setArguments(saveCommand, arguments);
                yield saveCommand.execute();
            }
            case "state" -> {
                Command stateCommand = new StateCommand(world, jsonObj);
                setArguments(stateCommand, arguments);
                yield stateCommand.execute();
            }
            case "robots" -> {
                Command listAllRobots = new ListAllRobotsCommand(world, jsonObj);
                setArguments(listAllRobots, arguments);
                yield listAllRobots.execute();
            }
            case "dump" -> {
                Command dumpCommand = new DumpCommand(world, jsonObj);
                setArguments(dumpCommand, arguments);
                yield dumpCommand.execute();
            }
            case "quit" -> {
                Command quitCommand = new QuitCommand(world, jsonObj);
                setArguments(quitCommand, arguments);
                yield quitCommand.execute();
            }
            case "turn" -> {
                Command turnCommand = new TurnCommand(world, jsonObj);
                setArguments(turnCommand, arguments);
                yield turnCommand.execute();
            }
            case "forward" -> {
                Command forwardCommand = new ForwardCommand(world, jsonObj);
                setArguments(forwardCommand, arguments);
                yield forwardCommand.execute();
            }
            case "back" -> {
                Command backwardCommand = new BackCommand(world, jsonObj);
                setArguments(backwardCommand, arguments);
                yield backwardCommand.execute();
            }
            case "fire" -> {
                Command fireCommand = new FireCommand(world, jsonObj);
                setArguments(fireCommand, arguments);
                yield fireCommand.execute();
            }
            case "reload" -> {
                Command reloadCommand = new ReloadCommand(world, jsonObj);
                setArguments(reloadCommand, arguments);
                yield reloadCommand.execute();
            }
            case "restore" -> {
                Command restoreCommand = new RestoreCommand(world, jsonObj);
                setArguments(restoreCommand, arguments);
                yield restoreCommand.execute();
            }
            case "repair" -> {
                Command repairCommand = new RepairCommand(world, jsonObj);
                setArguments(repairCommand, arguments);
                yield repairCommand.execute();
            }
            default -> protocol.getUnsupportedCommandError();
        };
    }
}
