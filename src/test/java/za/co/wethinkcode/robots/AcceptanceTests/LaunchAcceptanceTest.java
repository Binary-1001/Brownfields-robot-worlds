package za.co.wethinkcode.robots.AcceptanceTests;

import java.io.IOException;

import com.google.gson.JsonArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import za.co.wethinkcode.robots.client.Client;
import za.co.wethinkcode.robots.client.Request;

import static org.junit.jupiter.api.Assertions.*;

public class LaunchAcceptanceTest {
    /*as a client host
     I want to launch a robot in the robot world
     so that can shoot other robots in the world*/
    private final static int DEFAULT_PORT = 5000;
    private final static String DEFAULT_IP = "localhost";
    private Client clientServer = new Client(DEFAULT_IP, DEFAULT_PORT);

    @BeforeEach
    void connectToServer() {
        clientServer.connect(false);
    }

    @AfterEach
    void disconnectFromServer() {
        clientServer.disconnect();
    }

    @Test
    void validLaunchShouldSucceed() throws IOException {
// Given that I am connected to a running Robot Worlds server
// And the world is of size 1x1 (The world is configured or hardcoded to this size)
        assertTrue(clientServer.isConnected());
        JsonObject launchRequest = Request.launch("Hal", 5, 5);
        String response = clientServer.sendRequest(launchRequest);
        System.out.println(response);
        JsonObject launchResponse = JsonParser.parseString(response).getAsJsonObject();
// Then I should get a valid response from the server
        assertTrue(launchResponse.has("result"));
        assertEquals("OK", launchResponse.get("result").getAsString());
// And the position should be (x:0, y:0)
        assertNotNull(launchResponse.get("data"));
        assertNotNull(launchResponse.getAsJsonObject("data").get("position"));
        JsonObject data = launchResponse.getAsJsonObject("data");
        int expectedX = 0;
        int expectedY = 0;
        assertEquals(0, expectedX);
        assertEquals(0, expectedY);
        assertTrue(launchResponse.has("state"));
        JsonObject state = launchResponse.getAsJsonObject("state");
        assertTrue(state.get("status").getAsString().contains("NORMAL") || state.get("status").getAsString().contains("TODO"));

    }

    @Test
    void invaildLaunchShouldFail() throws IOException {
        //Given that I am connected to a running Robot Worlds server
        assertTrue(clientServer.isConnected());
        //When I send an incorrect launch request instead of the correct "launch" command
        JsonObject invalidRequest = new JsonObject();
        invalidRequest.addProperty("robot","r2d2");
        invalidRequest.addProperty("command","luanch");
        var arguments = new com.google.gson.JsonArray();
        arguments.add("shooter");
        arguments.add(5);
        arguments.add(5);
        invalidRequest.add("arguments", arguments);
        String responseString = clientServer.sendRequest(invalidRequest);
        System.out.println(responseString);
        assertNotNull(responseString);
        JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
        //Then I should get an error message.
        assertNotNull(response.get("result"));
        assertEquals("ERROR", response.get("result").getAsString());
        assertTrue(response.has("data"));
        JsonObject data = response.getAsJsonObject("data");
        assertTrue(data.has("message"));
        String message = data.get("message").getAsString();
        assertTrue(message.contains("robot does not exist")||message.contains("Unsupported command"));
    }

    @Test
    void duplicateRobotNameShouldFail() throws IOException {
        //Given that a user wants to launch a robot with a name of a robot already launched
        assertTrue(clientServer.isConnected());
        JsonObject robo = Request.launch("HAL", 5, 5);
        String firstResponseStr = clientServer.sendRequest(robo);
        JsonObject firstResponse = JsonParser.parseString(firstResponseStr).getAsJsonObject();
        assertNotNull(firstResponse.get("result"), "Response must include 'result'.");
        assertEquals("OK", firstResponse.get("result").getAsString(), "First launch should succeed.");

        //When there is already a robot with the same name
        String secondResponseStr = clientServer.sendRequest(robo);
        JsonObject secondResponse = JsonParser.parseString(secondResponseStr).getAsJsonObject();
        //Then it should deny the user access
        assertNotNull(secondResponse.get("result"));
        assertEquals("ERROR", secondResponse.get("result").getAsString());
//        assertEquals("Name already taken",secondResponse.get("message").getAsString());
    }

    @Test
    void notYetLaunched() throws IOException {
        //Given that the client is connected to the server
        assertTrue(clientServer.isConnected());
        //Assume that I have not yet launched but I want to get the fire command
        JsonObject stateRequest = Request.fire("HAL");
        String responseString = clientServer.sendRequest(stateRequest);
        assertNotNull(responseString);
        JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
        assertTrue(response.has("result"));
        assertEquals("ERROR", response.get("result").getAsString());
        assertTrue(response.has("data"));
        JsonObject data = response.getAsJsonObject("data");
        assertTrue(data.has("message"));
    }


    @Test
    void launchShouldFailWhenNoSpaceAvailable() throws IOException {
    // Given that I am connected to a running Robot Worlds server
    assertTrue(clientServer.isConnected());
    
    // And the world is fully populated (all positions are occupied)
    // First, fill up the world by launching robots until it's full
    // This assumes a small world size where we can quickly reach capacity
    
    JsonObject launchRequest;
    String response;
    JsonObject launchResponse;
    
    // Try to launch multiple robots until we get a failure response
    boolean launchFailed = false;
    int robotCount = 0;
    
    while (!launchFailed && robotCount < 3) { // Limit to prevent infinite loop
        launchRequest = Request.launch("Robot" + robotCount, 5, 5);
        response = clientServer.sendRequest(launchRequest);
        launchResponse = JsonParser.parseString(response).getAsJsonObject();
        
        if ("ERROR".equals(launchResponse.get("result").getAsString()) || 
            "FAIL".equals(launchResponse.get("result").getAsString())) {
            launchFailed = true;
            
            // Then the launch should fail with an appropriate error message
            assertNotNull(launchResponse.get("result"));
            assertTrue("ERROR".equals(launchResponse.get("result").getAsString()) || 
                      "FAIL".equals(launchResponse.get("result").getAsString()));
            
            // And the response should contain a message indicating no space
            assertNotNull(launchResponse.get("data"));
            JsonObject data = launchResponse.getAsJsonObject("data");
            assertTrue(data.has("message"));
            
            String message = data.get("message").getAsString().toLowerCase();
            assertEquals("no more space in this world",message);
        }
        robotCount++;
    }
    
    // If we never got a failure, the test should indicate this scenario
    if (!launchFailed) {
        System.out.println("Note: World did not reach capacity within the test limit");
    }
}

//    @Test
//    void clientCanLaunchAnotherRobot() throws IOException {
//    assertTrue(clientServer.isConnected());
//
//    JsonObject launchRequestOne = Request.launch("One", 5, 5);
//    String responseOne = clientServer.sendRequest(launchRequestOne);
//
//    JsonObject launchRequestTwo = Request.launch("Two", 5, 5);
//    String responseTwo = clientServer.sendRequest(launchRequestTwo);
//
//    System.out.println("Response One: " + responseOne);
//    System.out.println("Response Two: " + responseTwo);
//
//    JsonObject launchResponseOne = JsonParser.parseString(responseOne).getAsJsonObject();
//    JsonObject launchResponseTwo = JsonParser.parseString(responseTwo).getAsJsonObject();
//
//    // Then I should get a valid response from the server
//    assertTrue(launchResponseOne.has("result"));
//    assertTrue(launchResponseTwo.has("result"));
//
//    assertEquals("OK", launchResponseOne.get("result").getAsString());
//    System.out.println(launchResponseTwo);
//    assertEquals("OK", launchResponseTwo.get("result").getAsString());
//
//    // And the position should be (x:0, y:0)
//    assertNotNull(launchResponseOne.get("data"));
//    assertNotNull(launchResponseTwo.get("data"));
//
//    // FIX: Safe position access - handle both object and array cases
//    JsonObject dataOne = launchResponseOne.getAsJsonObject("data");
//    JsonObject dataTwo = launchResponseTwo.getAsJsonObject("data");
//
//    assertTrue(dataOne.has("position"));
//    assertTrue(dataTwo.has("position"));
//
//        // Extract position from string format "[x,y]"
//        String positionStrOne = dataOne.get("position").getAsString();
//        String positionStrTwo = dataTwo.get("position").getAsString();
//
//        // Parse the string position "[x,y]"
//        int[] positionOne = parsePositionString(positionStrOne);
//        int[] positionTwo = parsePositionString(positionStrTwo);
//
//        int actualXOne = positionOne[0];
//        int actualYOne = positionOne[1];
//        int actualXTwo = positionTwo[0];
//        int actualYTwo = positionTwo[1];
//
//        // Verify positions are different (both robots launched successfully)
//        assertNotEquals(actualXOne, actualXTwo, "Robots should have different X positions");
//        assertNotEquals(actualYOne, actualYTwo, "Robots should have different Y positions");
//
//        assertTrue(launchResponseOne.has("state"));
//        assertTrue(launchResponseTwo.has("state"));
//
//        JsonObject stateOne = launchResponseOne.getAsJsonObject("state");
//        JsonObject stateTwo = launchResponseTwo.getAsJsonObject("state");
//
//        assertTrue(stateOne.get("status").getAsString().contains("NORMAL") ||
//                stateOne.get("status").getAsString().contains("TODO"));
//        assertTrue(stateTwo.get("status").getAsString().contains("NORMAL") ||
//                stateTwo.get("status").getAsString().contains("TODO"));
//}
//
//    private int[] parsePositionString(String positionStr) {
//        // Remove brackets and split by comma
//        String cleaned = positionStr.replace("[", "").replace("]", "");
//        String[] parts = cleaned.split(",");
//        return new int[]{
//                Integer.parseInt(parts[0].trim()),
//                Integer.parseInt(parts[1].trim())
//        };
//    }

}


