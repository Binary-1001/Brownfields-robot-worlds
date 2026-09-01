package za.co.wethinkcode.robots.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.view.ObjectRecord;
import za.co.wethinkcode.robots.view.View;
import za.co.wethinkcode.robots.world.World;

import java.util.List;

/**
 * Command that allows a robot to look around and perceive objects in its field of view.
 * Returns a list of objects with their type, direction, and distance relative to the robot.
 */
public class LookCommand extends Command {
    public LookCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "look" command, retrieving visible objects from the robot's
     * perspective along with the robot's current state.
     *
     * @return a JsonObject containing visible objects and the robot's state
     */
    @Override
    public JsonObject execute() {
        String robotName = this.jsonObject.get("robot").getAsString();
        Robot robot = this.world.getRobotWithName(robotName);

        ProtocolResponse protocol = new ProtocolResponse();
        if (robot == null) {
            // Return error if robot not found
            return protocol.getArgumentsError();
        }

        // Get the robot's field of view and list all visible objects
        View fieldOfView = this.world.getFieldOfView(robot);

        JsonArray objectsArr = new JsonArray();
        List<ObjectRecord> objectRecords = fieldOfView.getAllObjects();
        for (ObjectRecord object : objectRecords) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("direction", object.direction().toString());
            jsonObj.addProperty("type", object.type().toString());
            jsonObj.addProperty("distance", object.distance());
            objectsArr.add(jsonObj);
        }

        // Construct response containing visible objects and robot's state
        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");

        JsonObject data = new JsonObject();
        data.add("objects", objectsArr);

        response.add("data", data);

        JsonObject state = protocol.getRobotState(robot);
        response.add("state", state);

        return response;
    }
}
