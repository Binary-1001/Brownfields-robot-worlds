package za.co.wethinkcode.robots.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.world.World;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for the {@link za.co.wethinkcode.robots.server.CreateWorld} class.
 *
 * This test suite verifies that the world is correctly created from a JSON config file.
 * It validates world size, configuration values (max bullets, shields, visibility),
 * and the correct parsing and placement of obstacles.
 */
public class CreateWorldTest {
    private static final String CONFIG_PATH = "src/test/java/za/co/wethinkcode/robots/server/serverConfigTest.json";
    private World world;

    @BeforeEach
    void setUp() {
        world = CreateWorld.loadFrom(CONFIG_PATH);
    }

    @Test
    @DisplayName("World dimensions match config")
    void testWorldSize() {
        assertEquals(20, world.getWidth());
        assertEquals(20, world.getHeight());
    }

    @Test
    @DisplayName("Max limits match config values")
    void testMaxAttributes() {
        assertEquals(3, world.getVisibilityRange());
        assertEquals(4, world.getMaxBullets());
        assertEquals(5, world.getMaxShieldStrength());
    }

    @Test
    @DisplayName("Correct number of obstacles added")
    void testObstacleCount() {
        List<Obstacle> obstacles = world.listObstacles();
        assertEquals(3, obstacles.size());
    }

    @Test
    @DisplayName("Obstacle positions are correctly parsed")
    void testObstaclePositions() {
        boolean found = false;
        for (Obstacle o : world.listObstacles()) {
            Position topLeft = o.getTopLeftCorner();
            Position bottomRight = o.getBottomRightCorner();
            if (topLeft.equals(new Position(0, 5)) && bottomRight.equals(new Position(3, 8))) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
