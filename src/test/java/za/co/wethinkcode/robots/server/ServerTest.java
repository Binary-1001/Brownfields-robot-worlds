package za.co.wethinkcode.robots.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.robots.world.World;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    //Different port to avoid clashes with the server
    private static final int TEST_PORT = 5001;
    private static Thread serverThread;

    @BeforeAll
    public static void startServer() throws InterruptedException {
        Server.PORT = TEST_PORT;

        serverThread = new Thread(() -> {
            try {
                World testWorld = CreateWorld.loadFrom("serverConfigTest.json");
                Server.startServer(testWorld);
            } catch (IOException e) {
                System.out.println("Server failed to start");
                System.out.println("Error: " + e.getMessage());
            }
        });

        serverThread.start();

        //Wait for the server to start up before running tests to avoid connection failure
        int attempts = 0;
        while (attempts < 10) {
            try (Socket s = new Socket("localhost", TEST_PORT)) {
                return; // Server is ready
            } catch (IOException e) {
                Thread.sleep(100);
            }
            attempts ++;
        }
        fail("Server did not start within timeout period");
            //Wait for the server to start up before running tests to avoid connection failure
            // try {
            //     Thread.sleep(500);
            // } catch (InterruptedException e) {
            //     throw new RuntimeException(e);
            // }
        // };
    }

    @AfterAll
    public static void stopServer() {
        Server.shutdown();
        try {
            serverThread.join(2000); // Wait up to 2 seconds for it to stop (closing client connections can take some time)
        } catch (InterruptedException e) {
            System.out.println("Server failed to stop");
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    public void testHelpMethod() {
        //Creates a fake output stream to capture what Server.help() prints
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        //Redirects System.out (where Java normally prints) to byteOutput stream
        System.setOut(new PrintStream(byteOutput));

        Server.help();

        String stringOutput = byteOutput.toString();
        assertTrue(stringOutput.contains("Commands available on the Robot World Server:"));
        assertTrue(stringOutput.contains("help - displays available commands on the Robot World Server."));
        assertTrue(stringOutput.contains("dump - displays the state of the world (robots, obstacles, etc.)."));
        assertTrue(stringOutput.contains("quit - disconnects all robots and ends the world."));
        assertTrue(stringOutput.contains("robots - lists all robots in the world and their state."));

        //Restores System.out to normal
        System.setOut(System.out);
    }

    @Test
    public void testOneClientConnection() throws IOException {
        try (Socket clientSocket = new Socket("localhost", TEST_PORT)) {
            assertTrue(clientSocket.isConnected());
        } catch (IOException e) {
            fail("Connection failed: " + e.getMessage());
        }
    }

    @Test
    public void testTwoClientConnections() throws IOException {
        try (Socket client1 = new Socket("localhost", TEST_PORT);
        Socket client2 = new Socket("localhost", TEST_PORT)) 
        {
            assertTrue(client1.isConnected());
            assertTrue(client2.isConnected());
        } catch (IOException e) {
            fail("Connection failed: " + e.getMessage());
        }
    }

    @Test
    public void testShutdownMethod() {
        //I made the running variable in the server protected so i can access it
        //Not sure if setting the intital state is necessary since we set it ourselves
        Server.running = true;
        Server.shutdown();
        assertFalse(Server.running);
    }
}
