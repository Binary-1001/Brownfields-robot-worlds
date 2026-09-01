package za.co.wethinkcode.robots.AcceptanceTests;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import za.co.wethinkcode.robots.client.Client;
import za.co.wethinkcode.robots.client.Request;

/**
 * As a player/user
 * I want my robot to be able to look around within the world's visible range
 * So that I can see what objects (obstacles, other robots, etc.) are around me.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LookAcceptanceTest {

    private final static int DEFAULT_PORT = 5000;
    private final static String DEFAULT_IP = "localhost";
    private Client clientServer;
    private static final String ROBOT_NAME = "r2d2";

    @BeforeEach
    void setUp() {
        clientServer = new Client(DEFAULT_IP, DEFAULT_PORT);
        connectToServer();
    }

    @AfterEach
    void tearDown() {
        clientServer.disconnect();
    }

    void connectToServer() {
        try {
            clientServer.connect(false);
        } catch (Exception e) {
            fail("Failed to connect to server: " + e.getMessage());
        }
    }

//    void disconnectFromServer() {
//        try {
//            if (clientServer != null) {
//                clientServer.disconnect();
//            }
//        } catch (Exception e) {
//            System.err.println("Error disconnecting from server: " + e.getMessage());
//        }
//    }

    @Test
    @Order(1)
    void validLookShouldSucceed() {
        // Given that the client is connected to the server
        assertTrue(clientServer.isConnected(), "Client should be connected to server");

        // And a robot has been successfully launched
        JsonObject launchRequest = Request.launch(ROBOT_NAME, 5, 5);
        String launchResponseString;
        try {
            launchResponseString = clientServer.sendRequest(launchRequest);
        } catch (IOException e) {
            fail("Failed to send launch request: " + e.getMessage());
            return;
        }

        JsonObject launchResponse = JsonParser.parseString(launchResponseString).getAsJsonObject();
        assertEquals("OK", launchResponse.get("result").getAsString(), "Robot should launch successfully");

        // When the client sends a valid look command
        JsonObject lookRequest = Request.look(ROBOT_NAME);
        String lookResponseString;
        try {
            lookResponseString = clientServer.sendRequest(lookRequest);
        } catch (IOException e) {
            fail("Failed to send look request: " + e.getMessage());
            return;
        }

        assertNotNull(lookResponseString, "Look response should not be null");
        JsonObject lookResponse = JsonParser.parseString(lookResponseString).getAsJsonObject();

        // Then the client should receive a valid response
        assertEquals("OK", lookResponse.get("result").getAsString(), "Look command should succeed");

        // Response should contain data with visible objects
        assertTrue(lookResponse.has("data"), "Response should contain data field");
        JsonObject data = lookResponse.getAsJsonObject("data");
        assertTrue(data.has("objects"), "Data should contain objects array");

        JsonArray objects = data.getAsJsonArray("objects");
        assertNotNull(objects, "Objects array should not be null");

        // Validate each object in the response
        for (int i = 0; i < objects.size(); i++) {
            JsonObject obj = objects.get(i).getAsJsonObject();
            assertTrue(obj.has("direction"), "Object " + i + " should have direction field");
            assertTrue(obj.has("type"), "Object " + i + " should have type field");
            assertTrue(obj.has("distance"), "Object " + i + " should have distance field");

            // Validate direction value is a valid compass direction
            String direction = obj.get("direction").getAsString();
            assertTrue(isValidDirection(direction), "Direction should be a valid compass direction: " + direction);

            // Validate distance is non-negative
            double distance = obj.get("distance").getAsDouble();
            assertTrue(distance >= 0, "Distance should be non-negative: " + distance);
        }
    }

    @Test
    @Order(2)
    void invalidLookCommandShouldFail() throws IOException {
        // Given robot is connected and launched
        assertTrue(clientServer.isConnected(), "Client should be connected to server");

        JsonObject launchRequest = Request.launch("Hal", 5, 5);
        String launchResponseString = clientServer.sendRequest(launchRequest);
        JsonObject launchResponse = JsonParser.parseString(launchResponseString).getAsJsonObject();
        assertNotNull(launchResponse, "Launch response should not be null.");
        System.out.println(launchResponse);
        assertEquals("OK", launchResponse.get("result").getAsString());


        // When the client sends an invalid command (e.g., "lok" instead of "look")
        JsonObject invalidLook = new JsonObject();
        invalidLook.addProperty("robot", "Hal");
        invalidLook.addProperty("command", "lok");
        invalidLook.add("arguments", new JsonArray()); // ✅ Correct: adds real JSON array, not a string

        String responseString = clientServer.sendRequest(invalidLook);

        // Defensive check for debugging
        System.out.println("Invalid Look Response: " + responseString);

        assertNotNull(responseString, "Server should respond even to invalid command.");
        JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();

        // Then the result should be an error
        assertEquals("ERROR", response.get("result").getAsString(), "Invalid command should return ERROR");
        assertTrue(response.has("data"), "Error response should contain data field");

        JsonObject data = response.getAsJsonObject("data");
        assertTrue(data.has("message"), "Error data should contain message field");

        String msg = data.get("message").getAsString();
        assertTrue(msg.toLowerCase().contains("unsupported")
                        || msg.toLowerCase().contains("unknown")
                        || msg.toLowerCase().contains("invalid"),
                "Error message should indicate unsupported/unknown command: " + msg);
    }

    @Test
    @Order(3)
    void lookWithoutLaunchingRobotShouldFail() {
        // Given client is connected but no robot is launched
        assertTrue(clientServer.isConnected(), "Client should be connected to server");

        // When sending look command without launching a robot
        JsonObject lookRequest = Request.look("nonexistent");
        String lookResponseString;
        try {
            lookResponseString = clientServer.sendRequest(lookRequest);
        } catch (IOException e) {
            fail("Failed to send look request: " + e.getMessage());
            return;
        }

        JsonObject lookResponse = JsonParser.parseString(lookResponseString).getAsJsonObject();

        // Then the response should be an error
        assertNotEquals("OK", lookResponse.get("result").getAsString(),
                "Look command without launched robot should not succeed");
    }

    /**
     * Helper method to validate compass directions
     */
    private boolean isValidDirection(String direction) {
        String[] validDirections = {"NORTH", "SOUTH", "EAST", "WEST", "NORTHEAST", "NORTHWEST", "SOUTHEAST", "SOUTHWEST"};
        for (String validDir : validDirections) {
            if (validDir.equals(direction)) {
                return true;
            }
        }
        return false;
    }
}




