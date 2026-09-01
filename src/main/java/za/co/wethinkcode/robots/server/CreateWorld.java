package za.co.wethinkcode.robots.server;

import com.google.gson.*;
import za.co.wethinkcode.robots.obstacle.*;
import za.co.wethinkcode.robots.world.World;
import za.co.wethinkcode.robots.position.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The CreateWorld class is responsible for loading and creating
 * a World instance from a JSON configuration file.
 *
 * It reads the configuration file, parses the JSON content,
 * builds the World object with its properties,
 * and loads obstacles into the world.
 */
public class CreateWorld {

    /**
     * Loads the world configuration from a JSON file.
     *
     * @param filePath the path to the JSON configuration file
     * @return a World object configured as specified in the JSON file,
     *         or a default World if an error occurs
     */
    public static World loadFrom(String filePath) {
        try {
            JsonObject config = parseJsonConfig(filePath);
            World world = buildWorld(config);
            loadObstacles(world, config);
            return world;
        } catch (Exception e) {
            System.err.println("Config error: " + e.getMessage());
            return new World(); // fallback to default empty world
        }
    }

    // Reads the JSON file and parses it into a JsonObject
    private static JsonObject parseJsonConfig(String filePath) throws IOException {
        Path path = Path.of(filePath);
        String jsonStr = Files.readString(path);
        return JsonParser.parseString(jsonStr).getAsJsonObject();
    }

    // Builds the World instance using values from the JSON object
    private static World buildWorld(JsonObject json) {
        int width = json.get("width").getAsInt();
        int height = json.get("height").getAsInt();
        int visibility = json.get("visibility").getAsInt();
        int maxBullets = json.get("maxBullets").getAsInt();
        int maxShields = json.get("maxShields").getAsInt();
        return new World(width, height, visibility, maxBullets, maxShields);
    }

    // Loads obstacles from the JSON config and adds them to the World
    private static void loadObstacles(World world, JsonObject json) {
        if (!json.has("obstacles")) return;  // no obstacles specified

        JsonArray obstacles = json.getAsJsonArray("obstacles");
        for (JsonElement element : obstacles) {
            JsonObject obj = element.getAsJsonObject();
            Obstacle obstacle = createObstacle(obj);
            world.addObstacle(obstacle);
        }
    }

    // Creates an obstacle object based on the type and coordinates
    private static Obstacle createObstacle(JsonObject obj) {
        String type = obj.get("type").getAsString();
        Position topLeft = getPosition(obj.getAsJsonArray("topLeft"));
        Position bottomRight = getPosition(obj.getAsJsonArray("bottomRight"));

        return switch (type.toLowerCase()) {
            case "lake" -> new Lake(topLeft, bottomRight);
            case "bottomlesspit" -> new BottomLessPit(topLeft, bottomRight);
            default -> new Mountain(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
        };
    }

    // Converts a JsonArray of two integers into a Position object
    private static Position getPosition(JsonArray arr) {
        return new Position(arr.get(0).getAsInt(), arr.get(1).getAsInt());
    }

    public static World fromArguments(int size, List<int[]> obstacles , List<int[]> pits,List<int[]> mines) {
        World world = new World(size);
        // for obstacles
        for (int[] obs : obstacles) {
            // each obs[] is {x, y}
            int x = obs[0];
            int y = obs[1];

            // Create a small 1x1 obstacle using Mountain
            world.addObstacle(new Mountain(x, y, x + 1, y + 1));
        }
        // for pits
        for (int[] p : pits) {
            Position tl = new Position(p[0], p[1]);
            Position br = new Position(p[0] + 4, p[1] + 4);
            world.addObstacle(new BottomLessPit(tl, br));
        }
        // for mines
        for (int[] m : mines) {
            world.addMine(new Mine(m[0], m[1]));
        }


        return world;
    }



}

