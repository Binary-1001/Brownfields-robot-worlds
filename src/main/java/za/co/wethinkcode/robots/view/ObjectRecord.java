package za.co.wethinkcode.robots.view;

import za.co.wethinkcode.robots.direction.Direction;

/**
 * ObjectRecord is a simple container that holds basic information about an object in the world.
 *
 * It stores:
 * - The type of object (e.g., robot, obstacle, boundary).
 * - The direction the object is in (e.g., NORTH, SOUTH).
 * - The distance of the object from the observer.
 *
 * This is useful for identifying and working with objects around a robot or agent.
 */
public record ObjectRecord(ObjectType type, Direction direction, int distance) {
}

