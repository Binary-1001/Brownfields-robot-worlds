package za.co.wethinkcode.robots.command;

import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

/**
 * Command that retrieves the current state of a specific robot in the world.
 * The state includes information such as the robot's position, direction, shield level, and shots remaining.
 */
public class StateCommand extends Command {
    public StateCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the state command to fetch the current state of the robot specified in the request.
     *
     * @return a JsonObject containing the result and the robot's state data
     */
    @Override
    public JsonObject execute() {
        String robotName = this.jsonObject.get("robot").getAsString();
        Robot robot = this.world.getRobotWithName(robotName);

        JsonObject response = new JsonObject();
        ProtocolResponse protocol = new ProtocolResponse();
        JsonObject data = new JsonObject();
        data.addProperty("visibility",world.getVisibilityRange());
        data.addProperty("position",robot.getPosition().toString());
        response.addProperty("result", "OK");
        response.add("state", protocol.getRobotState(robot));
        response.add("data",data);
        return response;
    }
}
