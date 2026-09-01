package za.co.wethinkcode.robots.command;

import com.google.gson.JsonObject;

import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

/**
 * Command that reloads the ammunition of a specified robot.
 */
public class ReloadCommand extends Command {
    public ReloadCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "reload" command, causing the specified robot to reload its ammunition.
     *
     * @return a JsonObject confirming the reload action and returning the robot's updated state
     * @throws InterruptedException if the reload process is interrupted
     */
    @Override
    public JsonObject execute() throws InterruptedException {
        String robotName = this.jsonObject.get("robot").getAsString();
        Robot robot = this.world.getRobotWithName(robotName);

        // Perform reload operation on the robot
        robot.reload();

        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");

        JsonObject data = new JsonObject();
        data.addProperty("message", "Done");
        response.add("data", data);

        // Include updated robot state in the response
        ProtocolResponse protocol = new ProtocolResponse();
        JsonObject state = protocol.getRobotState(robot);
        response.add("state", state);

        return response;
    }
}
