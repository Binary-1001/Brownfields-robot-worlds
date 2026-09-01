package za.co.wethinkcode.robots.protocol;

import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.view.ObjectRecord;
import za.co.wethinkcode.robots.view.View;
import za.co.wethinkcode.robots.world.World;

import java.util.List;

import com.google.gson.JsonArray;
import za.co.wethinkcode.robots.position.Position;

/**
 * Provides standard JSON responses for all robot commands and error states.
 * This class follows the game protocol specification and is used by command handlers
 * to produce valid responses to the client in a consistent structure.
 */
public class ProtocolResponse implements ProtocolResponseInterface {

    /**
     * Constructs the robot state section for a protocol response.
     *
     * @param robot the robot to extract state from
     * @return a JsonObject with position, direction, shields, shots, and status
     */
    @Override
    public JsonObject getRobotState(Robot robot) {
        JsonObject response = new JsonObject();
        response.addProperty("position", robot.getPosition().toString());
        response.addProperty("direction", robot.getOrientation().toString());
        response.addProperty("shields", robot.getShieldStrength());
        response.addProperty("shots", robot.getBulletsAvail());

        String status;
        if (!robot.isAlive()) status = "DEAD";
        else if (robot.isReloadMode()) status = "RELOAD";
        else if (robot.isRepairMode()) status = "REPAIR";
        else status = "NORMAL";

        response.addProperty("status", status);

        return response;
    }

    /**
     * Returns a JSON response indicating an invalid location error.
     *
     * @return a standardized error message
     */
    @Override
    public JsonObject getLocationError() {
        JsonObject error = new JsonObject();
        error.addProperty("result", "ERROR");
        error.addProperty("message", "Invalid location");
        return error;
    }

    /**
     * Returns a JSON response indicating a duplicate robot name error.
     *
     * @return a standardized error message
     */
    @Override
    public JsonObject getNameAlreadyTakenError() {
        JsonObject error = new JsonObject();
        error.addProperty("result", "ERROR");
        error.addProperty("message", "Name already taken");
        return error;
    }

    /**
     * Generates the result of a 'look' command including objects in the field of view.
     *
     * @param world the world to get visibility from
     * @param robot the robot whose view is calculated
     * @return the full JSON response with object list and robot state
     */
    @Override
    public JsonObject getView(World world, Robot robot) {
        View fieldOfView = world.getFieldOfView(robot);

        JsonArray objectsArr = new JsonArray();
        List<ObjectRecord> objectRecords = fieldOfView.getAllObjects();
        for (ObjectRecord object : objectRecords) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("direction", object.direction().toString());
            jsonObj.addProperty("type", object.type().toString());
            jsonObj.addProperty("distance", object.distance());
            objectsArr.add(jsonObj);
        }

        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");

        JsonObject data = new JsonObject();
        data.add("objects", objectsArr);

        response.add("data", data);

        JsonObject state = this.getRobotState(robot);
        response.add("state", state);

        return response;
    }

    /**
     * Constructs a JSON object with robot status info for debugging/state checks.
     *
     * @param robot the robot to describe
     * @return robot position, visibility, shield level, and reload/repair time
     */
    @Override
    public JsonObject getRobotData(Robot robot) {
        JsonObject data = new JsonObject();
        Position pos = robot.getPosition();

        data.addProperty("position", pos.toString());
        data.addProperty("visibility", robot.getVisibilityRange());
        data.addProperty("reload", 0);
        data.addProperty("repair", 0);
        data.addProperty("shields", robot.getShieldStrength());
        return data;
    }

    /**
     * Constructs a JSON response for a successful movement or action command.
     *
     * @param message description of the result
     * @param robot the robot that performed the action
     * @return structured success response with updated robot state
     */
    @Override
    public JsonObject getMoveResponse(String message, Robot robot) {
        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");
        response.addProperty("message", message);

        JsonObject state = getRobotState(robot);
        response.add("state", state);
        
        return response;
    }

    /**
     * Constructs a standardized error for unknown commands.
     *
     * @return error response indicating unsupported command
     */
    @Override
    public JsonObject getUnsupportedCommandError() {
        JsonObject error = new JsonObject();
        error.addProperty("result", "ERROR");
        JsonObject data = new JsonObject();
        data.addProperty("message", "Unsupported command");
        error.add("data",data);
        return error;
    }

    /**
     * Constructs a standardized error for unexpected or malformed arguments.
     *
     * @return error response for bad argument usage
     */
    @Override
    public JsonObject getArgumentsError() {
        JsonObject error = new JsonObject();
        error.addProperty("result", "ERROR");
        error.addProperty("message", "Unexpected arguments");
        return error;
    }
}
