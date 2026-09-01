package za.co.wethinkcode.robots.command;

import java.util.List;

import com.google.gson.JsonObject;

import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.protocol.ProtocolResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

/**
 * Command that turns a robot either left or right based on the direction specified in the arguments.
 * The robot's orientation is updated accordingly.
 */
public class TurnCommand extends Command {
    public TurnCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    private Direction turnLeft(Direction curDirection) {
        Direction newDirection;

        switch (curDirection) {
            case NORTH:
                newDirection = Direction.WEST;
                break;
            case WEST:
                newDirection = Direction.SOUTH;
                break;
            case SOUTH:
                newDirection = Direction.EAST;
                break;
            case EAST:
                newDirection = Direction.NORTH;
                break;
            default:
                newDirection = curDirection;
        }

        return newDirection;
    }

    private Direction turnRight(Direction curDirection) {
        Direction newDirection;

        switch (curDirection) {
            case NORTH:
                newDirection = Direction.EAST;
                break;
            case WEST:
                newDirection = Direction.NORTH;
                break;
            case SOUTH:
                newDirection = Direction.WEST;
                break;
            case EAST:
                newDirection = Direction.SOUTH;
                break;
            default:
                newDirection = curDirection;
        }

        return newDirection;
    }

    /**
     * Executes the turn command to rotate the specified robot in the requested direction.
     *
     * @return a JsonObject containing the result, message, and the robot's updated state
     */
    @Override
    public JsonObject execute() {
        String robotName = this.jsonObject.get("robot").getAsString();
        Robot robot = this.world.getRobotWithName(robotName);
        Direction robotDirection = robot.getOrientation();

        List<Object> args = this.getArguments();
        String turn = (String) args.get(0);

        Direction newDirection = turn.toLowerCase().equals("left")
            ? this.turnLeft(robotDirection)
            : this.turnRight(robotDirection);

        robot.updateDirection(newDirection);

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
