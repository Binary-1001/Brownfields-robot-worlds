package za.co.wethinkcode.robots.command;

import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.world.World;

/**
 * Command that signals the server to shut down.
 */
public class QuitCommand extends Command {
    public QuitCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "quit" command, returning a confirmation message
     * indicating the server is shutting down.
     *
     * @return a JsonObject confirming the server shutdown command
     */
    @Override
    public JsonObject execute() {
        JsonObject response = new JsonObject();
        response.addProperty("result", "OK");
        response.addProperty("message", "Shutting down server...");
        return response;
    }
}
