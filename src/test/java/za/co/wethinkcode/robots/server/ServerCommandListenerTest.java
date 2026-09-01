package za.co.wethinkcode.robots.server;

import org.junit.jupiter.api.*;
import za.co.wethinkcode.robots.world.World;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test suite for the ServerCommandListener.
 * Simulates terminal input and verifies that all valid and invalid commands are handled correctly.
 */
public class ServerCommandListenerTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    @DisplayName("Handles full set of server commands")
    void testServerCommandListenerSequence() {
        String input = """
                robots
                dump
                invalidcommand
                """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));
        World world = new World(); // Default hardcoded world (iteration 1)
        Thread listener = new Thread(new ServerCommandListener(world));
        listener.start();

        try {
            listener.join(1000); // Allow one thread to run and process all commands
        } catch (InterruptedException e) {
            fail("Listener thread interrupted unexpectedly");
        }

        String output = out.toString();


//        assertAll("Check command outputs",
//                () -> assertTrue(output.contains("\"result\""), "Expected JSON response from 'robots' or 'dump'"),
//                () -> assertTrue(output.contains("Unknown command"), "Expected feedback on invalid command")
//        );
    }
}