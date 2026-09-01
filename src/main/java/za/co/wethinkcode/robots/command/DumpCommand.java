package za.co.wethinkcode.robots.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.world.World;

import java.util.List;

/**
 * Command that returns a diagnostic dump of the current world state,
 * including all obstacles and active robots.
 */
public class DumpCommand extends Command {
    public DumpCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "dump" command, returning a JSON object that includes
     * all obstacles and robot states in the current world.
     *
     * @return a JsonObject containing the world's obstacles and robots
     * @throws InterruptedException if the command execution is interrupted
     */
    @Override
    public JsonObject execute() throws InterruptedException {
        JsonObject response = new JsonObject();

        // Gather and serialize all obstacles
        List<Obstacle> obstacles = this.world.listObstacles();
        JsonArray obstacleArr = new JsonArray();
        for (Obstacle obstacle : obstacles) {
            JsonObject obstacleObj = new JsonObject();
            obstacleObj.addProperty("topLeftCorner", obstacle.getTopLeftCorner().toString());
            obstacleObj.addProperty("bottomRightCorner", obstacle.getBottomRightCorner().toString());
            obstacleArr.add(obstacleObj);
        }

        response.add("obstacles", obstacleArr);

        // Include the current list of all robots in the world
        Command listAllRobotsCommand = new ListAllRobotsCommand(this.world, this.jsonObject);
        JsonObject robots = listAllRobotsCommand.execute();
        response.add("robots", robots);

        return response;
    }
}
