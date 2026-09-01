package za.co.wethinkcode.robots.webAPI;

import io.javalin.http.Context;
import za.co.wethinkcode.robots.obstacle.Mine;
import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.persistence.RestoreWorldData;
import za.co.wethinkcode.robots.persistence.WorldData;
import za.co.wethinkcode.robots.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ApiHandler provides static methods for handling HTTP requests to the Robot Worlds Web API.
 * Handles world retrieval and JSON serialization.
 */
public class ApiHandler {

    /**
     * Handles GET /world/{world}
     * Retrieves a specific world by name from the database and returns it as JSON.
     *
     * @param context the Javalin HTTP context
     * @param world   the World instance (will be modified with restored data)
     */
    public static void getWorld(Context context, World world) {
        String worldName = context.pathParam("world");

        RestoreWorldData restoreService = new RestoreWorldData();
        WorldData worldData = restoreService.loadWorld(worldName);

        if (worldData == null) {
            context.status(404)
                    .json(Map.of("error", "World '" + worldName + "' not found in database"));
            return;
        }

        // Restore the world from database
        restoreService.restoreWorld(world, worldData);

        // Build JSON response
        Map<String, Object> result = buildWorldJson(world, worldName);
        context.status(200).json(result);
    }

    /**
     * Handles GET /world
     * Returns the current world state as JSON.
     *
     * @param context the Javalin HTTP context
     * @param world   the World instance
     */
    public static void getCurrentWorld(Context context, World world) {
        Map<String, Object> result = buildWorldJson(world, "current_world");
        context.status(200).json(result);
    }

    /**
     * Builds a JSON representation of the world.
     *
     * @param world the World instance
     * @param name  the name to use in JSON
     * @return a map representing the world as JSON
     */
    private static Map<String, Object> buildWorldJson(World world, String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("width", world.getWidth());
        result.put("height", world.getHeight());

        // Add obstacles
        List<Map<String, Object>> obstaclesList = new ArrayList<>();
        for (Obstacle o : world.getObstaclesList()) {
            Map<String, Object> obstacle = new HashMap<>();

            // Get the type as a string
            String obstacleType = o.getClass().getSimpleName().toLowerCase();
            obstacle.put("type", obstacleType);

            obstacle.put("x", o.getTopLeftCorner().getX());
            obstacle.put("y", o.getTopLeftCorner().getY());
            obstacle.put("width", o.getBottomRightCorner().getX() - o.getTopLeftCorner().getX() + 1);
            obstacle.put("height", o.getBottomRightCorner().getY() - o.getTopLeftCorner().getY() + 1);
            obstaclesList.add(obstacle);
        }
        result.put("obstacles", obstaclesList);

        // Add mines
        List<Map<String, Object>> minesList = new ArrayList<>();
        for (Mine m : world.getMinesList()) {
            Map<String, Object> mine = new HashMap<>();
            mine.put("type", "mine");
            mine.put("x", m.getPosition().getX());
            mine.put("y", m.getPosition().getY());
            minesList.add(mine);
        }
        result.put("mines", minesList);

        return result;
    }
}