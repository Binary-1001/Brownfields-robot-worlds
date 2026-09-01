package za.co.wethinkcode.robots.command;

import com.google.gson.JsonObject;

import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

/**
 * Command that repairs a robot by restoring its shield or health capacity.
 */
public class RepairCommand extends Command {
    public RepairCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "repair" command for the specified robot.
     * The robot's state is updated to reflect restored shields or health.
     *
     * @return a JsonObject confirming the repair and providing the robot's updated state
     * @throws InterruptedException if the repair process is interrupted
     */
    @Override
    public JsonObject execute() throws InterruptedException {
        String robotName = this.jsonObject.get("robot").getAsString();
        Robot robot = this.world.getRobotWithName(robotName);

        robot.repair();

        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");

        JsonObject data = new JsonObject();
        data.addProperty("message", "Done");
        response.add("data", data);

        ProtocolResponse protocol = new ProtocolResponse();
        JsonObject state = protocol.getRobotState(robot);
        response.add("state", state);

        return response;
    }
}
