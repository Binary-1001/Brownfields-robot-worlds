package za.co.wethinkcode.robots.obstacle;

import za.co.wethinkcode.robots.position.Position;

/**
 * The Obstacle class represents a rectangular area on a map or grid
 * that something (like a robot or player) might not be able to walk over or see through.
 * 
 * It has a top-left and bottom-right corner to define its size and position.
 * 
 * This class is abstract, which means you can't use it directly—you have to extend it in another class.
 */
public abstract class Obstacle {
    // These are the corners of the obstacle, which define its size and position.
    private final Position TOP_LEFT_CORNER;
    private final Position BOTTOM_RIGHT_CORNER;

    // Constructor that takes in four coordinates to create the corners of the obstacle.
    public Obstacle(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        this(new Position(topLeftX, topLeftY), new Position(bottomRightX, bottomRightY));
    }

    // Constructor that uses two Position objects to define the obstacle corners.
    public Obstacle(Position topLeftCorner, Position bottomRightCorner) {
        this.TOP_LEFT_CORNER = topLeftCorner;
        this.BOTTOM_RIGHT_CORNER = bottomRightCorner;
    }

    /**
     * Checks if a given position (like a robot wanting to move) is inside this obstacle.
     * 
     * @param position - the position we are checking
     * @return true if the position is inside the obstacle
     */
    public boolean isPositionInside(Position position) {
        Position topLeftCorner = getTopLeftCorner();
        Position bottomRightCorner = getBottomRightCorner();

        // Check if the X and Y coordinates are within the obstacle's boundaries
        boolean inBetweenX = (position.getX() >= topLeftCorner.getX()) && (position.getX() <= bottomRightCorner.getX());
        boolean inBetweenY = (position.getY() >= topLeftCorner.getY()) && (position.getY() <= bottomRightCorner.getY());
        return inBetweenX && inBetweenY;
    }

    /**
     * Checks if a movement from one point to another goes through this obstacle.
     * 
     * @param src - starting position
     * @param dst - ending position
     * @return true if the line from src to dst crosses the obstacle
     */
    public boolean interceptsPath(Position src, Position dst) {
        Position pointOfIntersectionA;
        Position pointOfIntersectionB;

        // If movement is horizontal (same Y), we check X direction.
        if (src.getY() == dst.getY()) {
            pointOfIntersectionA = new Position(this.TOP_LEFT_CORNER.getX(), src.getY());
            pointOfIntersectionB = new Position(this.BOTTOM_RIGHT_CORNER.getX(), src.getY());
        } else {
            // Otherwise, we assume movement is vertical and check Y direction.
            pointOfIntersectionA = new Position(src.getX(), this.TOP_LEFT_CORNER.getY());
            pointOfIntersectionB = new Position(src.getX(), this.BOTTOM_RIGHT_CORNER.getY());
        }

        // Check if either of those points fall inside the obstacle.
        boolean pointAIntersect = pointOfIntersectionA.isIn(this.TOP_LEFT_CORNER, this.BOTTOM_RIGHT_CORNER);
        boolean pointBIntersect = pointOfIntersectionB.isIn(this.TOP_LEFT_CORNER, this.BOTTOM_RIGHT_CORNER);

        return pointAIntersect || pointBIntersect;
    }

    /**
     * Tells us whether we can see through this obstacle.
     * Default is false (we cannot see through).
     */
    public boolean isSeeThrough() { return false; }

    /**
     * Tells us whether we can walk over this obstacle.
     * Default is false (we cannot walk over).
     */
    public boolean canWalkOver() { return false; }

    // These methods return the corners of the obstacle.
    public Position getTopLeftCorner() { return this.TOP_LEFT_CORNER; }
    public Position getBottomRightCorner() { return this.BOTTOM_RIGHT_CORNER; }
}
