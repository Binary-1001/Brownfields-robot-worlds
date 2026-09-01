package za.co.wethinkcode.robots.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.persistence.SaveWorldData;
import za.co.wethinkcode.robots.world.World;

public class SaveCommand extends Command {

    public SaveCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    @Override
    public JsonObject execute() throws InterruptedException {
        JsonObject response = new JsonObject();
        SaveWorldData saveWorldData = new SaveWorldData();

        try {
            JsonArray args = jsonObject.getAsJsonArray("arguments");
            if (args == null || args.size() == 0) {
                response.addProperty("result", "ERROR");
                response.addProperty("message", "Missing or empty 'name' property for SAVE command");
                return response;
            }

            String name = args.get(0).getAsString().trim();
            if (name.isEmpty()) {
                response.addProperty("result", "ERROR");
                response.addProperty("message", "Missing or empty 'name' property for SAVE command");
                return response;
            }

            int worldId = saveWorldData.saveWorld(name, world.getWidth(), world.getHeight());
            if (worldId == -1) {
                response.addProperty("result", "ERROR");
                response.addProperty("message", "Failed to save world: " + name);
                return response;
            }

            saveWorldData.saveObstaclesAndPits(worldId, world.listObstacles());
            saveWorldData.saveMines(worldId, world.getMinesList());

            response.addProperty("result", "OK");
            response.addProperty("message", "World saved as '" + name + "'");
            response.addProperty("world_id", worldId);

        } catch (Exception e) {
            response.addProperty("result", "ERROR");
            response.addProperty("message", "Error saving world: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}
