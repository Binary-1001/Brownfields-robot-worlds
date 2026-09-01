package za.co.wethinkcode.robots.view;

/**
 * ObjectType defines the different types of objects that can exist in the world.
 *
 * Types include:
 * - ROBOT: Represents another robot.
 * - OBSTACLE: Represents something that blocks movement, like a mountain or lake.
 * - EDGE: Represents the edge or boundary of the world.
 */
public enum ObjectType {
    ROBOT,
    OBSTACLE,
    EDGE;
}

