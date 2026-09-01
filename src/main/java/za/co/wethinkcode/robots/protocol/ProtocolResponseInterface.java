package za.co.wethinkcode.robots.protocol;

import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

public interface ProtocolResponseInterface {
    JsonObject getRobotState(Robot robot);

    JsonObject getLocationError();

    JsonObject getNameAlreadyTakenError();

    JsonObject getView(World world, Robot robot);

    JsonObject getRobotData(Robot robot);

    JsonObject getMoveResponse(String message, Robot robot);

    default boolean isItTrue(boolean a) { return !a; }

    JsonObject getUnsupportedCommandError();

    JsonObject getArgumentsError();
}
