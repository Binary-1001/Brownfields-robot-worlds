package za.co.wethinkcode.robots.webAPI;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.*;
import za.co.wethinkcode.robots.obstacle.BottomLessPit;
import za.co.wethinkcode.robots.obstacle.Mine;
import za.co.wethinkcode.robots.obstacle.Mountain;
import za.co.wethinkcode.robots.persistence.DatabaseConnection;
import za.co.wethinkcode.robots.persistence.SaveWorldData;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.world.World;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GET /world endpoint as per story requirements.
 *
 * Story Requirements:
 * 1. GET /world - return current world objects as JSON
 * 2. GET /world/{world} - restore objects from database and return as JSON
 */
class GetWorldEndpointTest {
    private static final int TEST_PORT = 8888;
    private static ApiServer server;
    private static World world;
    private static DatabaseConnection db;
    private static SaveWorldData saveService;

    @BeforeAll
    static void startServer() {
        // Initialize database
        db = new DatabaseConnection();
        saveService = new SaveWorldData();
        Connection conn = db.connection();
        if (conn != null) {
            db.runSchema(conn);
        }

        // Create a world with some objects for testing
        world = new World(15, 15, 5, 3, 3);

        // Add obstacles to the world
        Mountain mountain = new Mountain(2, 2, 6, 6);
        world.addObstacle(mountain);

        BottomLessPit pit = new BottomLessPit(new Position(8, 8), new Position(12, 12));
        world.addObstacle(pit);

        // Add mines to the world
        Mine mine1 = new Mine(5, 5);
        Mine mine2 = new Mine(10, 10);
        world.addMine(mine1);
        world.addMine(mine2);

        // Start API server
        server = new ApiServer(world);
        server.start(TEST_PORT);
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    @DisplayName("GET /world - returns current world with all objects as JSON")
    void getCurrentWorld() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:" + TEST_PORT + "/world").asJson();

        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaders().getFirst("Content-Type"));

        JSONObject jsonObject = response.getBody().getObject();

        // Verify JSON structure
        assertEquals("current_world", jsonObject.get("name"));
        assertEquals(15, jsonObject.get("width"));
        assertEquals(15, jsonObject.get("height"));

        // Verify obstacles
        JSONArray obstacles = jsonObject.getJSONArray("obstacles");
        assertEquals(2, obstacles.length(), "Should return all obstacles in current world");

//        // Verify mines
//        JSONArray mines = jsonObject.getJSONArray("mines");
//        assertEquals(2, mines.length(), "Should return all mines in current world");

        // Verify obstacle structure
        JSONObject firstObstacle = obstacles.getJSONObject(0);
        assertTrue(firstObstacle.has("type"));
        assertTrue(firstObstacle.has("x"));
        assertTrue(firstObstacle.has("y"));

//        // Verify mine structure
//        JSONObject firstMine = mines.getJSONObject(0);
//        assertEquals("mine", firstMine.get("type"));
//        assertTrue(firstMine.has("x"));
//        assertTrue(firstMine.has("y"));
    }

    @Test
    @DisplayName("GET /world/{world} - restores world from database and returns as JSON")
    void getNamedWorldFromDatabase() throws UnirestException {
        // Save a world to the database
        int worldId = saveService.saveWorld("test_world", 20, 20);
        assertTrue(worldId > 0, "World should be saved to database");

        // Add obstacles and mines to database
        Mountain mountain = new Mountain(3, 3, 7, 7);
        saveService.saveObstaclesAndPits(worldId, List.of(mountain));

        Mine mine = new Mine(10, 10);
        saveService.saveMines(worldId, List.of(mine));

        // Request the world via API
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:" + TEST_PORT + "/world/test_world").asJson();

        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaders().getFirst("Content-Type"));

        JSONObject jsonObject = response.getBody().getObject();

        // Verify the restored world
        assertEquals("test_world", jsonObject.get("name"));
        assertEquals(20, jsonObject.get("width"));
        assertEquals(20, jsonObject.get("height"));

        // Verify objects were restored
        JSONArray obstacles = jsonObject.getJSONArray("obstacles");
        assertTrue(obstacles.length() >= 1, "Should restore obstacles from database");

        JSONArray mines = jsonObject.getJSONArray("mines");
        assertTrue(mines.length() >= 1, "Should restore mines from database");
    }

    @Test
    @DisplayName("GET /world/{world} - returns 404 when world not found in database")
    void getNamedWorldNotFound() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:" + TEST_PORT + "/world/nonexistent_world").asJson();

        assertEquals(404, response.getStatus());

        JSONObject jsonObject = response.getBody().getObject();
        assertTrue(jsonObject.has("error"), "404 response should include error message");
        assertTrue(jsonObject.getString("error").contains("not found"));
    }

    @Test
    @DisplayName("GET /world - JSON structure matches GET /world/{world}")
    void bothEndpointsReturnConsistentStructure() throws UnirestException {
        // Get current world
        HttpResponse<JsonNode> currentResponse = Unirest.get("http://localhost:" + TEST_PORT + "/world").asJson();
        JSONObject currentWorld = currentResponse.getBody().getObject();

        // Save and get named world
        int worldId = saveService.saveWorld("structure_test", 15, 15);
        Mountain mountain = new Mountain(2, 2, 6, 6);
        saveService.saveObstaclesAndPits(worldId, List.of(mountain));

        HttpResponse<JsonNode> namedResponse = Unirest.get("http://localhost:" + TEST_PORT + "/world/structure_test").asJson();
        JSONObject namedWorld = namedResponse.getBody().getObject();

        // Both should have same keys
        assertTrue(currentWorld.has("name"));
        assertTrue(currentWorld.has("width"));
        assertTrue(currentWorld.has("height"));
        assertTrue(currentWorld.has("obstacles"));
        assertTrue(currentWorld.has("mines"));

        assertTrue(namedWorld.has("name"));
        assertTrue(namedWorld.has("width"));
        assertTrue(namedWorld.has("height"));
        assertTrue(namedWorld.has("obstacles"));
        assertTrue(namedWorld.has("mines"));

        // Both should return application/json
        assertEquals("application/json", currentResponse.getHeaders().getFirst("Content-Type"));
        assertEquals("application/json", namedResponse.getHeaders().getFirst("Content-Type"));
    }
}