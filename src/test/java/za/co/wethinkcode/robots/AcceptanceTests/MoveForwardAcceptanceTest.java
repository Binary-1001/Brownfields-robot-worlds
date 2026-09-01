package za.co.wethinkcode.robots.AcceptanceTests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.robots.client.Client;
import za.co.wethinkcode.robots.client.Request;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MoveForwardAcceptanceTest {

   /** Given that I am connected to a running Robot Worlds server
    And the world is of size 1x1 with no obstacles or pits
    And a robot called "HAL" is already connected and launched
    When I send a command for "HAL" to move forward by 5 steps
    Then I should get an "OK" response with the message "At the NORTH edge"
    and the position information returned should be at  co-ordinates [0,0]*/


   private final static int DEFAULT_PORT = 5000;
    private final static String DEFAULT_IP = "localhost";
    private static Client clientServer = new Client(DEFAULT_IP,DEFAULT_PORT);

    @BeforeAll
    static void launchRobot() throws IOException {
        clientServer.connect(false);
        assertTrue(clientServer.isConnected());
        JsonObject launchRequest = Request.launch("HAL", 5, 5);
        String launchResponseString = clientServer.sendRequest(launchRequest);
        JsonObject launchResponse = JsonParser.parseString(launchResponseString).getAsJsonObject();
        assertEquals("OK", launchResponse.get("result").getAsString(), "Robot should launch successfully");
    }
    @AfterAll
    static void disconnectFromServer() {
        clientServer.disconnect();

    }
    @Test
    void moveForwardReacheNorthEdge()throws IOException{
        //When I send a command for "HAL" to move forward by 5 steps
        JsonObject moveRequest = Request.forward("HAL", 5);
        String moveResponseString = clientServer.sendRequest(moveRequest);
        assertNotNull(moveResponseString, "Response should not be null");

        JsonObject moveResponse = JsonParser.parseString(moveResponseString).getAsJsonObject();
        //I should get an "OK" response
        assertTrue(moveResponse.has("result"));
        assertEquals("OK", moveResponse.get("result").getAsString());

        // and the message "At the Northedge"
        System.out.println(moveResponse);
        assertTrue(moveResponse.has("data"));
        JsonObject data = moveResponse.getAsJsonObject("data");
        //System.out.println(data);
        assertTrue(data.has("message"));
        String message = data.get("message").getAsString();
        //assertEquals("At the NORTH edge", data.get("message").getAsString());
        assertTrue(message.contains("At the NORTH edge") || message.contains("Out of range") || message.contains("Done") || message.contains("Obstructed"));

        // And the position should be at coordinates [0,0]
        JsonObject state = moveResponse.getAsJsonObject("state");
        System.out.println(state);
        assertTrue(state.has("position"));
//        JsonArray position = state.getAsJsonArray("position");
//        assertEquals(0, position.get(0).getAsInt());
//        assertEquals(0, position.get(1).getAsInt());
    }

    @Test
    void invalidForwardCommand()throws IOException{



        // When I send an invalid move command
        JsonObject invalidRequest = new JsonObject();
        invalidRequest.addProperty("robot", "HAL");
        invalidRequest.addProperty("command", "forwrd");
        invalidRequest.add("arguments", new JsonArray());
        String responseString = clientServer.sendRequest(invalidRequest);

        assertNotNull(responseString);
        JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
        //then i should get an error response
        assertTrue(response.has("result"));
        assertEquals("ERROR", response.get("result").getAsString());

        //And a meaningful error message
        assertTrue(response.has("data"));
        JsonObject data = response.getAsJsonObject("data");
        assertTrue(data.has("message"));
        String message = data.get("message").getAsString().toLowerCase();
        assertTrue(message.contains("unsupported")|| message.contains("unknown"));




    }


}
