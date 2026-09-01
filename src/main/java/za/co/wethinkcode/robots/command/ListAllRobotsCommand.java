package za.co.wethinkcode.robots.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

import java.util.List;

/**
 * Command that retrieves a list of all robots currently present in the world,
 * along with their states.
 */
public class ListAllRobotsCommand extends Command {
    public ListAllRobotsCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "robots" command, returning the current state of all robots
     * in the world as a JSON array.
     *
     * @return a JsonObject containing an array of robot states
     */
    @Override
    public JsonObject execute() {
        List<Robot> robots = this.world.listRobots();

        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");

        ProtocolResponse protocol = new ProtocolResponse();
        JsonArray robotArr = new JsonArray();

        // Collect state information for each robot
        for (Robot robot : robots) {
            JsonObject state = protocol.getRobotState(robot);
            robotArr.add(state);
        }

        response.add("data", robotArr);
        return response;
    }
}
