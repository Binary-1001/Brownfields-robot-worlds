package za.co.wethinkcode.robots.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

public class CommandTest {
    private World world;
    private static final String ROBOT_NAME = "PearlTheDestroyer";

    @BeforeEach
    public void setup() {
        world = new World(20, 20, 10, 5, 5);
    }

    private JsonObject reformatObject(JsonObject obj) {
        return JsonParser.parseString(obj.toString()).getAsJsonObject();
    }

    private JsonObject createCommand(String command, Object... args) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("robot", ROBOT_NAME);
        jsonInput.addProperty("command", command);
        
        JsonArray arguments = new JsonArray();
        for (Object arg : args) {
            if (arg instanceof String) {
                arguments.add((String) arg);
            } else if (arg instanceof Number) {
                arguments.add((Number) arg);
            }
        }
        jsonInput.add("arguments", arguments);
        return jsonInput;
    }

    private void addRobot() {
        world.addRobot(new Robot(ROBOT_NAME, 5));
    }

    private void assertBasicSuccess(JsonObject response) {
        assertEquals("OK", response.get("result").getAsString());
    }

    private void assertHasStateFields(JsonObject state) {
        String[] fields = {"position", "direction", "shields", "shots", "status"};
        for (String field : fields) {
            assertTrue(state.has(field), "Missing state field: " + field);
        }
    }

    @Test
    void testLaunchCommand() throws InterruptedException {
        JsonObject command = createCommand("launch", "Normal", 25, 75);
        JsonObject response = Command.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        assertTrue(response.get("data").getAsJsonObject().has("position"));
        assertHasStateFields(response.get("state").getAsJsonObject());
    }

    @Test
    void testStateCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("state");
        JsonObject response = StateCommand.manage(world, reformatObject(command));

        assertHasStateFields(response.get("state").getAsJsonObject());
    }

    @Test
    void testListAllRobots() throws InterruptedException {
        addRobot();
        JsonObject command = new JsonObject();
        command.addProperty("command", "robots");
        command.add("arguments", new JsonArray());

        JsonObject response = ListAllRobotsCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        assertEquals(1, response.get("data").getAsJsonArray().size());
    }

    @Test
    void testQuitCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("quit");
        JsonObject response = QuitCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
    }

    @Test
    void testForwardCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("forward", 10);
        JsonObject response = ForwardCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        assertHasStateFields(response.get("state").getAsJsonObject());
    }

    @Test
    void testBackCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("back", 5);
        JsonObject response = BackCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        JsonObject state = response.get("state").getAsJsonObject();
        assertTrue(state.has("position"));
        assertEquals("NORTH", state.get("direction").getAsString());
    }

    @Test
    void testTurnLeftCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("turn", "left");
        JsonObject response = TurnCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        assertEquals("WEST", response.get("state").getAsJsonObject().get("direction").getAsString());
    }

    @Test
    void testTurnRightCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("turn", "right");
        JsonObject response = TurnCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        assertEquals("EAST", response.get("state").getAsJsonObject().get("direction").getAsString());
    }

    @Test
    void testRepairCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("repair");
        JsonObject response = RepairCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        assertEquals("Done", response.get("data").getAsJsonObject().get("message").getAsString());
        assertEquals("REPAIR", response.get("state").getAsJsonObject().get("status").getAsString());
    }

    @Test
    void testReloadCommand() throws InterruptedException {
        addRobot();
        JsonObject command = createCommand("reload");
        JsonObject response = ReloadCommand.manage(world, reformatObject(command));

        assertBasicSuccess(response);
        assertEquals("Done", response.get("data").getAsJsonObject().get("message").getAsString());
        assertEquals("RELOAD", response.get("state").getAsJsonObject().get("status").getAsString());
    }
}