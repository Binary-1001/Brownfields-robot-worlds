package za.co.wethinkcode.robots.AcceptanceTests.iteration2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.robots.client.Client;
import za.co.wethinkcode.robots.client.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class Launch2by2AcceptanceTest {
    /*-Given a world of size 2x2
      -and the world has an obstacle at coordinate [1,1]*/
    private Process serverProcess;
    private Client client;
    private final int PORT = 5000;
    private final String HOST = "localhost";
    private  List<String> robots = List.of("a","b","c","d","e","f","g","h");

    @BeforeEach
    void startServer() throws IOException, InterruptedException {
        // Fixed JAR filename - removed duplicate .jar
        ProcessBuilder startServer = new ProcessBuilder(
                "java", "-jar", "libs/reference-server-0.2.3.jar",
                "-p", String.valueOf(PORT),
                "-s", "2",
                "-o", "1,1"
        );

        startServer.inheritIO();
        serverProcess = startServer.start();

        // Give the server more time to start
        Thread.sleep(2000);

        // Try to connect with retries
        client = new Client(HOST, PORT);
        int maxRetries = 10;
        boolean connected = false;

        for (int i = 0; i < maxRetries; i++) {
            try {
                client.connect(false);
                if (client.isConnected()) {
                    connected = true;
                    System.out.println("✓ Connected to server on attempt " + (i + 1));
                    break;
                }
            } catch (Exception e) {
                System.out.println("Connection attempt " + (i + 1) + " failed: " + e.getMessage());
                if (i < maxRetries - 1) {
                    Thread.sleep(500);
                }
            }


        }

        assertTrue(connected, "Client should connect to the server after " + maxRetries + " attempts");

        //I have successfully launched 8 robots into the world
        for (String robot : robots){
            JsonObject launchRequest = Request.launch(robot,5,5);
            String response = client.sendRequest(launchRequest);
            JsonObject launchResponse = JsonParser.parseString(response).getAsJsonObject();
            assertEquals("OK",launchResponse.get("result").getAsString());
        }
    }

    @AfterEach
    void closeServer() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        if (serverProcess != null) {
            serverProcess.destroy();
            try {
                // Wait for process to terminate
                serverProcess.waitFor();
            } catch (InterruptedException e) {
                serverProcess.destroyForcibly();
            }
        }
    }

    @Test
    void worldWithAnObstacleIsFull() throws IOException {
        assertTrue(client.isConnected());
        /*When I launch one more robot
      -Then I should get an error response back with the message "No more space in this world"*/
        JsonObject launchrequest9 = Request.launch("nhlaksDaKiller",5,5);
        String response9 = client.sendRequest(launchrequest9);
        JsonObject launchResponse9 = JsonParser.parseString(response9).getAsJsonObject();
//        System.out.println(launchResponse9);
        assertEquals("ERROR",launchResponse9.get("result").getAsString());
        assertTrue(launchResponse9.has("data"));
        JsonObject data = launchResponse9.getAsJsonObject("data");
        assertNotNull(data);
        assertTrue(data.has("message"));
        String message = data.get("message").getAsString();
        assertTrue(message.contains("No more space in this world"));

    }

    @Test
    void launchRobotsIntoAWorldWithAnObstacle() throws IOException {
        assertTrue(client.isConnected());

        List<String> positions = new ArrayList<>();

        // Check each robot’s position (already launched in @BeforeEach)
        for (String robot : robots) {
            JsonObject stateRequest = Request.state(robot);
            String response = client.sendRequest(stateRequest);
            JsonObject stateResponse = JsonParser.parseString(response).getAsJsonObject();

            assertEquals("OK", stateResponse.get("result").getAsString());

            JsonObject data = stateResponse.getAsJsonObject("data");
            var positionArray = data.getAsJsonArray("position");
            int x = positionArray.get(0).getAsInt();
            int y = positionArray.get(1).getAsInt();

            String posStr = "[" + x + "," + y + "]";
            positions.add(posStr);

            // Assert robot not on obstacle
            assertFalse(x == 1 && y == 1, "Robot " + robot + " at " + posStr + " - obstacle position!");
        }

        assertFalse(positions.contains("[1,1]"),
                "No robot should be at obstacle position [1,1]");

        System.out.println("All robot positions: " + positions);
    }
}
