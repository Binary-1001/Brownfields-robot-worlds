package za.co.wethinkcode.robots.robot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.world.World;

/**
 * Test suite for the {@link za.co.wethinkcode.robots.robot.Robot} class.
 *
 * This suite verifies correct initialization, behavior, and internal state changes
 * of a Robot including movement, direction updates, shield damage, repair mode,
 * reload mode, and proper enum values for movement responses.
 */
public class RobotTest {
    Robot robot;
    Position position;

    @BeforeEach
    void setUp() {
        robot = new Robot("TestBot", 5, 5, 3);
        new World().addRobot(robot);
        position = robot.getPosition();
    }

    // Iteration 1 Tests
    @Test
    @DisplayName("Test Robot Basic Properties")
    void testRobotProperties() {
        assertEquals("TestBot", robot.getName());
        assertEquals(3, robot.getVisibilityRange());
        assertEquals(position, robot.getPosition());

        assertNotNull(robot.getOrientation());
        assertEquals(Direction.NORTH, robot.getOrientation());
    }

    @Test
    @DisplayName("Test Robot Visibility Range is Positive")
    void testVisibilityRangeIsPositive() {
        int visibility = robot.getVisibilityRange();
        assertTrue(visibility > 0);
        assertEquals(3, visibility);
        assertNotEquals(0, visibility);
    }

    @Test
    @DisplayName("Test Robot Direction is Set Correctly")
    void testDirectionIsCorrect() {
        assertEquals(Direction.NORTH, robot.getOrientation());
        assertNotEquals(Direction.SOUTH, robot.getOrientation());
        assertTrue(robot.getOrientation() instanceof Direction);
    }

    @Test
    @DisplayName("Test Robot Name Validity")
    void testRobotName() {
        String name = robot.getName();
        assertNotNull(name);
        assertEquals("TestBot", name);
        assertTrue(name.startsWith("Test"));
    }

    @Test
    @DisplayName("Robot returns correct bullet count")
    void testGetBulletsAvail() {
        assertNotNull(robot.getBulletsAvail());
        assertEquals(5, robot.getBulletsAvail());

    }

    @Test
    @DisplayName("Robot alive status is true")
    void testIsAlive() {
        assertTrue(robot.isAlive());
    }

    @Test
    @DisplayName("Robot fires and bullet count decreases")
    void testFire() {
        assertEquals(5, robot.getBulletsAvail());

        robot.fire();
        assertEquals(4, robot.getBulletsAvail());
    }

    @Test
    @DisplayName("Robot dies and isAlive becomes false")
    void testDie() {
        assertTrue(robot.isAlive());
        robot.die();
        assertFalse(robot.isAlive());
    }

    @Test
    @DisplayName("Robot direction updates correctly")
    void testUpdateDirection() {
        robot.updateDirection(Direction.EAST);
        assertEquals(Direction.EAST, robot.getOrientation());
    }

    @Test
    @DisplayName("MoveResponse enum contains SUCCESS")
    void testSuccessEnum() {
        MoveResponse response = MoveResponse.SUCCESS;
        assertEquals("SUCCESS", response.name());
        assertSame(MoveResponse.SUCCESS, response);
    }

    @Test
    @DisplayName("MoveResponse enum contains OBSTRUCTED")
    void testObstructedEnum() {
        MoveResponse response = MoveResponse.OBSTRUCTED;
        assertEquals("OBSTRUCTED", response.name());
        assertSame(MoveResponse.OBSTRUCTED, response);
    }

    @Test
    @DisplayName("MoveResponse enum contains OUT_OF_RANGE")
    void testOutOfRangeEnum() {
        MoveResponse response = MoveResponse.OUT_OF_RANGE;
        assertEquals("OUT_OF_RANGE", response.name());
        assertSame(MoveResponse.OUT_OF_RANGE, response);
    }

    @Test
    @DisplayName("Robot takes damage and shield strength decreases")
    void testTakeDamage() {
        assertEquals(5, robot.getShieldStrength());
        robot.takeDamage();
        assertEquals(4, robot.getShieldStrength());
    }

    @Test
    @DisplayName("Robot enters repair mode and repairs shield")
    void testRepairShield() throws InterruptedException {
        assertEquals(5, robot.getShieldStrength());
        assertFalse(robot.isRepairMode());

        robot.takeDamage();
        assertEquals(4, robot.getShieldStrength());

        robot.repair();
        assertTrue(robot.isRepairMode());
    }

    @Test
    @DisplayName("isRepairMode returns true when robot is repairing and false otherwise")
    void testIsRepairMode() throws InterruptedException {
        assertFalse(robot.isRepairMode());
        robot.repair();
        assertTrue(robot.isRepairMode());
    }

    @Test
    @DisplayName("isReloadMode returns true when robot is repairing and false otherwise")
    void testIsReloadMode() throws InterruptedException {
        assertFalse(robot.isReloadMode());
        robot.reload();
        assertTrue(robot.isReloadMode());
    }
}
