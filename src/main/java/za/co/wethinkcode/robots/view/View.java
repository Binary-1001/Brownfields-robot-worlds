package za.co.wethinkcode.robots.view;

import za.co.wethinkcode.robots.direction.Direction;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The View class implements the ViewInterface and helps keep track of all visible objects.
 * 
 * It stores all objects (like robots, obstacles, and boundaries), and lets you:
 * - Add new objects with direction and distance
 * - Get lists of objects in each direction (North, South, East, West)
 * - Get only certain types of objects (e.g., only robots or only obstacles)
 */
public class View implements ViewInterface {

    // List to store all visible objects in the world
    List<ObjectRecord> objects = new ArrayList<>();

    /**
     * Adds a new object to the world with its type, direction, and distance.
     */
    @Override
    public void createObject(ObjectType type, Direction direction, int distance) {
        objects.add(new ObjectRecord(type, direction, distance));
    }

    /**
     * Returns all objects that are located in the northern direction.
     */
    @Override
    public List<ObjectRecord> getObjectsInTheNorthernDirection() {
        return objects.stream()
                .filter(obj -> obj.direction() == Direction.NORTH)
                .toList();
    }

    /**
     * Returns all objects that are located in the southern direction.
     */
    @Override
    public List<ObjectRecord> getObjectsInTheSouthernDirection() {
        return objects.stream()
                .filter(obj -> obj.direction() == Direction.SOUTH)
                .toList();
    }

    /**
     * Returns all objects that are located in the eastern direction.
     */
    @Override
    public List<ObjectRecord> getObjectsInTheEasternDirection() {
        return objects.stream()
                .filter(obj -> obj.direction() == Direction.EAST)
                .toList();
    }

    /**
     * Returns all objects that are located in the western direction.
     */
    @Override
    public List<ObjectRecord> getObjectsInTheWesternDirection() {
        return objects.stream()
                .filter(obj -> obj.direction() == Direction.WEST)
                .toList();
    }

    /**
     * Returns all objects stored in the list.
     */
    @Override
    public List<ObjectRecord> getAllObjects() {
        return objects;
    }

    /**
     * Returns only the objects that are obstacles.
     */
    @Override
    public List<ObjectRecord> getObstacles() {
        return objects.stream()
                .filter(obj -> obj.type() == ObjectType.OBSTACLE)
                .toList();
    }

    /**
     * Returns only the objects that are robots.
     */
    @Override
    public List<ObjectRecord> getRobots() {
        return objects.stream()
                .filter(obj -> obj.type() == ObjectType.ROBOT)
                .toList();
    }

    /**
     * Returns only the objects that are boundaries or edges of the world.
     */
    @Override
    public List<ObjectRecord> getBoundaries() {
        return objects.stream()
                .filter(obj -> obj.type() == ObjectType.EDGE)
                .toList();
    }
}

