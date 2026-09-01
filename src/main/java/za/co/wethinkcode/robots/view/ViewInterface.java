package za.co.wethinkcode.robots.view;

import za.co.wethinkcode.robots.direction.Direction;

import java.util.List;

/**
 * ViewInterface is used to interact with and observe objects in the world (like robots or obstacles).
 * 
 * It helps us:
 * - Create objects in the world.
 * - Get a list of objects in specific directions.
 * - Get lists of all objects, or just certain types (like only robots or only obstacles).
 * 
 * Think of this as a way to "see" what's around or manage what exists in the environment.
 */
public interface ViewInterface {

    /**
     * Creates an object in the world.
     * 
     * @param type      - the type of object to create (e.g., Robot, Obstacle)
     * @param direction - the direction to place it from the current position
     * @param distance  - how far away from the current position
     */
    void createObject(ObjectType type, Direction direction, int distance);

    // Returns a list of objects that are in the northern direction.
    List<ObjectRecord> getObjectsInTheNorthernDirection();

    // Returns a list of objects that are in the southern direction.
    List<ObjectRecord> getObjectsInTheSouthernDirection();

    // Returns a list of objects that are in the eastern direction.
    List<ObjectRecord> getObjectsInTheEasternDirection();

    // Returns a list of objects that are in the western direction.
    List<ObjectRecord> getObjectsInTheWesternDirection();

    // Returns a list of all objects in the world.
    List<ObjectRecord> getAllObjects();

    // Returns a list of only obstacles in the world.
    List<ObjectRecord> getObstacles();

    // Returns a list of all robots in the world.
    List<ObjectRecord> getRobots();

    // Returns a list of boundaries (like walls or limits of the world).
    List<ObjectRecord> getBoundaries();
}
