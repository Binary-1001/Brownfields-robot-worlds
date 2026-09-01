package za.co.wethinkcode.robots.view;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.robots.direction.Direction;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ViewTest {
    @Test
    public void testGetAllObjects() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.WEST, 3);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);

        List<ObjectRecord> objectRecords = view.getAllObjects();
        assertEquals(4, objectRecords.size());

        // Test if getAllObjects returns a list with two Obstacle objects
        Stream<ObjectRecord> stream = objectRecords.stream().filter((obj -> obj.type() == ObjectType.OBSTACLE));

        assertEquals(2, stream.count());
    }

    @Test
    public void testGetAllObjectsTwo() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);

        List<ObjectRecord> objectRecords = view.getAllObjects();
        assertEquals(3, objectRecords.size());

        // Test if getAllObjects returns a list with two Obstacle objects
        Stream<ObjectRecord> stream = objectRecords.stream().filter((obj -> obj.type() == ObjectType.OBSTACLE));

        assertEquals(1, stream.count());
    }

    @Test
    public void testGetObstacles() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.WEST, 3);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);

        List<ObjectRecord> obstacles = view.getObstacles();

        assertEquals(2, obstacles.size());

        boolean allObjectsAreObstacles = obstacles.stream().allMatch(obj -> obj.type() == ObjectType.OBSTACLE);
        assertTrue(allObjectsAreObstacles);
    }

    @Test
    public void testGetRobots() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.WEST, 3);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);

        List<ObjectRecord> robots = view.getRobots();

        assertEquals(1, robots.size());
        assertSame(ObjectType.ROBOT, robots.get(0).type());
    }

    @Test
    public void testGetBoundaries() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.WEST, 3);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);
        view.createObject(ObjectType.EDGE, Direction.SOUTH, 7);

        List<ObjectRecord> boundaries = view.getBoundaries();

        assertEquals(2, boundaries.size());

        boolean allAreBoundaries = boundaries.stream().allMatch(obj -> obj.type()==ObjectType.EDGE);
        assertTrue(allAreBoundaries);
    }

    @Test
    public void testGetObjectsInTheNorthernDirection() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.NORTH, 3);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);
        view.createObject(ObjectType.EDGE, Direction.SOUTH, 7);

        List<ObjectRecord> objectsInTheNorthernDirection = view.getObjectsInTheNorthernDirection();

        assertEquals(2, objectsInTheNorthernDirection.size());

        boolean allFacingNorth = objectsInTheNorthernDirection.stream()
                .allMatch(obj -> obj.direction() == Direction.NORTH);

        assertTrue(allFacingNorth);
    }

    @Test
    public void testGetObjectsInTheSouthernDirection() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.NORTH, 3);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);
        view.createObject(ObjectType.EDGE, Direction.SOUTH, 7);

        List<ObjectRecord> objectsInTheSouthernDirection = view.getObjectsInTheSouthernDirection();

        assertEquals(2, objectsInTheSouthernDirection.size());

        boolean allFacingSouth = objectsInTheSouthernDirection.stream()
                .allMatch(obj -> obj.direction() == Direction.SOUTH);

        assertTrue(allFacingSouth);
    }

    @Test
    public void testGetObjectsInTheEasternDirection() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.NORTH, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.NORTH, 3);
        view.createObject(ObjectType.ROBOT, Direction.SOUTH, 7);
        view.createObject(ObjectType.EDGE, Direction.SOUTH, 7);

        List<ObjectRecord> objectsInTheEasternDirection = view.getObjectsInTheEasternDirection();

        assertEquals(1, objectsInTheEasternDirection.size());

        boolean allFacingEast = objectsInTheEasternDirection.stream()
                .allMatch(obj -> obj.direction() == Direction.EAST);

        assertTrue(allFacingEast);
    }

    @Test
    public void testGetObjectsInTheWesternDirection() {
        View view = new View();
        view.createObject(ObjectType.OBSTACLE, Direction.EAST, 10);
        view.createObject(ObjectType.EDGE, Direction.WEST, 14);
        view.createObject(ObjectType.OBSTACLE, Direction.NORTH, 3);
        view.createObject(ObjectType.ROBOT, Direction.WEST, 7);
        view.createObject(ObjectType.EDGE, Direction.WEST, 7);

        List<ObjectRecord> objectsInTheWesternDirection = view.getObjectsInTheWesternDirection();

        assertEquals(3, objectsInTheWesternDirection.size());

        boolean allFacingWest = objectsInTheWesternDirection.stream()
                .allMatch(obj -> obj.direction() == Direction.WEST);

        assertTrue(allFacingWest);
    }
}
