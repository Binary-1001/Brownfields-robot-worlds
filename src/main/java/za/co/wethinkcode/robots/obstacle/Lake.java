package za.co.wethinkcode.robots.obstacle;

import za.co.wethinkcode.robots.position.Position;

/**
 * The Lake class is a type of Obstacle.
 * It represents a lake on the map that you cannot walk over,
 * but you can see through it (e.g., to spot something on the other side).
 */
public class Lake extends Obstacle {

    // This constructor creates a lake using the top-left and bottom-right corners.
    public Lake(Position topLeftCorner, Position bottomRightCorner) {
        super(topLeftCorner, bottomRightCorner);
    }

    /**
     * Lakes can be seen through, so we override the default behavior to return true.
     */
    @Override
    public boolean isSeeThrough() {
        return true;
    }
}

