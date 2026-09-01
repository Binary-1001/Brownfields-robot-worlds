package za.co.wethinkcode.robots.obstacle;

/**
 * The Mountain class is a type of Obstacle.
 * It represents a mountain area on the map that cannot be walked over or seen through.
 * 
 * This class uses the Obstacle class to set its size and position using two corners.
 */
public class Mountain extends Obstacle {

    // This constructor passes the coordinates to the Obstacle class to create the mountain shape.
    public Mountain(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        super(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }
}
