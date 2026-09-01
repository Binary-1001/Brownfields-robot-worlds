package za.co.wethinkcode.robots.AcceptanceTests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import za.co.wethinkcode.robots.client.Client;
import za.co.wethinkcode.robots.client.Request;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class StateAcceptanceTest {
    /*As a player/user
      I want to get the state of the robot
      So that I can retrieve the current state of my robot in the world.*/
    private final static int DEFAULT_PORT = 5000;
    private final static String DEFAULT_IP = "localhost";
    private static Client clientServer = new Client(DEFAULT_IP,DEFAULT_PORT);

    @BeforeAll
    static void launchRobt() throws IOException {
        clientServer.connect(false);

        //launching the robot
        JsonObject launchRobo = Request.launch("r2d2",5,5);
        String launchReponseString = clientServer.sendRequest(launchRobo);
        JsonObject launchResponse = JsonParser.parseString(launchReponseString).getAsJsonObject();
        assertEquals("OK",launchResponse.get("result").getAsString());
    }

    @AfterAll
    static void disconnectServer() {
        clientServer.disconnect();
    }


    @Test
    void validStateShouldSucceed() throws IOException {
        /*-Given that the client is connected to the server
    -and the robot is launched successfully*/
        assertTrue(clientServer.isConnected());

        //-When the client sends a valid state request to the server
        JsonObject stateRequest = Request.state("r2d2");
        String stateResponseString = clientServer.sendRequest(stateRequest);
        System.out.println(stateResponseString);
        assertNotNull(stateResponseString);
        JsonObject stateResponse = JsonParser.parseString(stateResponseString).getAsJsonObject();

        //-Then the client should get a valid response from the server
        assertTrue(stateResponse.has("result"));
        assertEquals("OK",stateResponse.get("result").getAsString());
        assertTrue(stateResponse.has("data"));
        JsonObject data = stateResponse.getAsJsonObject("data");
        assertTrue(data.has("position"));
        assertTrue(stateResponse.has("state"));
        JsonObject state = stateResponse.getAsJsonObject("state");
        assertEquals("NORTH",state.get("direction").getAsString());
        assertTrue(state.has("shields") || state.has("shots") || state.has("status"));
    }

    @Test
    void invalidStateShouldfail() throws IOException {
        /*
        - Given that the client is connected to the server
        - and the robot is launched successfully*/
        assertTrue(clientServer.isConnected());

        //- When the client sends an invalid state request with a command such as "stete" instead of "state"
        JsonObject invalidRequest = new JsonObject();
        invalidRequest.addProperty("robot", "r2d2");
        invalidRequest.addProperty("command", "stete");
        invalidRequest.add("arguments", new JsonArray());
        String responseString = clientServer.sendRequest(invalidRequest);
        System.out.println(responseString);
        assertNotNull(responseString);
        JsonObject invalidResponse = JsonParser.parseString(responseString).getAsJsonObject();
        assertTrue(invalidResponse.has("result"));
        assertEquals("ERROR", invalidResponse.get("result").getAsString());
        assertTrue(invalidResponse.has("data"));
        JsonObject data = invalidResponse.getAsJsonObject("data");
        assertTrue(data.has("message"));
        String message = data.get("message").getAsString();
        assertTrue(message.toLowerCase().contains("unsupported") || message.toLowerCase().contains("unknown"));
    }

    @Test
    void notYetLaunched() throws IOException {
        //Given that the client is connected to the server
        assertTrue(clientServer.isConnected());
        //Assume that I have not yet launched but I want to get the state command
        JsonObject stateRequest = Request.state("r");
        String responseString = clientServer.sendRequest(stateRequest);
        System.out.println(responseString);
        assertNotNull(responseString, "Server response should not be null.");
        JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
        assertTrue(response.has("result"));
        assertEquals("ERROR", response.get("result").getAsString());
        assertTrue(response.has("data"));
        JsonObject data = response.getAsJsonObject("data");
        assertTrue(data.has("message"));
        String message = data.get("message").getAsString();
        assertTrue(message.toLowerCase().contains("robot does not exist"));
    }

}
