package za.co.wethinkcode.robots.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.persistence.RestoreWorldData;
import za.co.wethinkcode.robots.persistence.WorldData;
import za.co.wethinkcode.robots.world.World;

public class RestoreCommand extends Command {

    public RestoreCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    @Override
    public JsonObject execute() throws InterruptedException {
        JsonObject response = new JsonObject();
        RestoreWorldData loader = new RestoreWorldData();

        try {
            JsonArray args = jsonObject.getAsJsonArray("arguments");
            if (args == null || args.size() == 0) {
                response.addProperty("result", "ERROR");
                response.addProperty("message", "Missing or empty 'name' property for RESTORE command");
                return response;
            }

            String name = args.get(0).getAsString().trim();
            if (name.isEmpty()) {
                response.addProperty("result", "ERROR");
                response.addProperty("message", "Missing or empty 'name' property for RESTORE command");
                return response;
            }

            WorldData worldData = loader.loadWorld(name);
            if (worldData == null) {
                response.addProperty("result", "ERROR");
                response.addProperty("message", "No world found with name '" + name + "'");
                return response;
            }

            loader.restoreWorld(world, worldData);

            response.addProperty("result", "OK");
            response.addProperty("message", "World restored from '" + name + "'");
            response.addProperty("world_id", worldData.getWorldId());

        } catch (Exception e) {
            response.addProperty("result", "ERROR");
            response.addProperty("message", "Error restoring world: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}
