package za.co.wethinkcode.robots.client;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for validating command parsing and input validation logic
 * handled by the {@link za.co.wethinkcode.robots.client.CommandHandler} class.
 *
 * This suite verifies correct command recognition, argument validation,
 * suggestion output for mistyped commands, and help message display.
 */
public class CommandHandlerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Validates launch with correct args")
    void testLaunchValidation() {
        boolean valid = CommandHandler.validateUserInput("bot launch 3 4");
        assertTrue(valid);
    }

    @Test
    @DisplayName("Rejects invalid command and prints suggestions")
    void testInvalidCommandSuggestion() {
        CommandHandler.validateUserInput("Optimus launc 3 4");
        String output = outContent.toString();
        assertTrue(output.contains("Did you mean to try"));
    }

    @Test
    @DisplayName("Rejects invalid argument for command - forward")
    void testForwardInvalidArg() {
        boolean valid = CommandHandler.validateUserInput("BumbleBee forward three");
        assertFalse(valid);
    }

    @Test
    @DisplayName("Rejects not enough args")
    void testNotEnoughArgs() {
        boolean valid = CommandHandler.validateUserInput("Megatron");
        assertFalse(valid);
    }

    @Test
    @DisplayName("Prints help when help command given")
    void testHelpPrints() {
        CommandHandler.validateUserInput("Smokescreen help");
        String output = outContent.toString();
        assertTrue(output.contains("Project Description"));
    }
}
