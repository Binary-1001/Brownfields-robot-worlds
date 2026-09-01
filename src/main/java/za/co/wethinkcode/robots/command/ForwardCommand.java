package za.co.wethinkcode.robots.command;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.internal.LazilyParsedNumber;

import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.MoveResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

/**
 * Command that moves a robot forward by a specified number of steps.
 * Handles movement success, obstruction, or out-of-range results.
 */
public class ForwardCommand extends Command {
    public ForwardCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "forward" command for the specified robot, moving it a given number
     * of steps in the direction it is currently facing.
     *
     * @return a JsonObject representing the result of the movement attempt
     */
    @Override
    public JsonObject execute() {
        List<Object> args = this.getArguments();

        String robotName = this.jsonObject.get("robot").getAsString();
        Robot robot = this.world.getRobotWithName(robotName);

        // Attempt to move the robot forward by the specified number of steps
        MoveResponse moveResponse = robot.move(((LazilyParsedNumber) args.get(0)).intValue());

        JsonObject data = new JsonObject();
        switch (moveResponse) {
            case SUCCESS:
                data.addProperty("message", "Done");
                break;

            case OBSTRUCTED:
                data.addProperty("message", "Obstructed");
                break;

            case OUT_OF_RANGE:
                data.addProperty("message", "Out of range");
                break;
        }

        // Build the response with movement result and updated robot state
        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");
        response.add("data", data);

        ProtocolResponse protocol = new ProtocolResponse();
        JsonObject state = protocol.getRobotState(robot);

        response.add("state", state);
        return response;
    }
}
