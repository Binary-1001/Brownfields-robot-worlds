package za.co.wethinkcode.robots.command;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.internal.LazilyParsedNumber;

import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

/**
 * Command that launches a new robot into the world with the specified
 * name, shield strength, and number of shots.
 */
public class LaunchCommand extends Command {
    public LaunchCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "launch" command, creating and registering a new robot
     * with the provided name and capabilities, if the name is not already taken.
     *
     * @return a JsonObject indicating success or failure of the launch operation
     */
    @Override
    public JsonObject execute() {
        List<Object> args = this.getArguments();

        int maxShieldStrength = ((LazilyParsedNumber) args.get(1)).intValue();
        int maxShots = ((LazilyParsedNumber) args.get(2)).intValue();

        String robotName = this.jsonObject.get("robot").getAsString();

        // Create a new robot with a fixed visibility of 10
        Robot robot = new Robot(robotName, maxShieldStrength, maxShots, 10);

        ProtocolResponse protocol = new ProtocolResponse();

        // Reject if a robot with the same name already exists
        if (world.getRobotWithName(robotName) != null) {
            return protocol.getNameAlreadyTakenError();
        }

        // Add robot to the world
        world.addRobot(robot);

        // Build response with data and initial state
        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");

        JsonObject data = protocol.getRobotData(robot);
        response.add("data", data);

        JsonObject state = protocol.getRobotState(robot);
        response.add("state", state);

        return response;
    }
}
