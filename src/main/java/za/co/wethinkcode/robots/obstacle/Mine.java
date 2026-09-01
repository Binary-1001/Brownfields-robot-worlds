package za.co.wethinkcode.robots.obstacle;

import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.robot.Robot;

/**
 * The Mine class represents an explosive mine placed in the world.
 *
 * Mines are NOT obstacles (they do not block movement or vision),
 * but stepping on them triggers an explosion that kills the robot.
 */
public class Mine {

    private Position position;  // Where the mine is located
    private boolean active;     // Whether the mine can still explode

    /**
     * Create a mine at a specific (x, y) position. Mines start active.
     */
    public Mine(int x, int y) {
        this.position = new Position(x, y);
        this.active = true;
    }

    /**
     * Create a mine from a Position object.
     */
    public Mine(Position position) {
        this.position = position;
        this.active = true;
    }

    /**
     * Detonate the mine if a robot steps on it.
     */
    public void explode(Robot robot) {
        if (active) {
            robot.die();      // kill the robot
            active = false;   // mine is now used
        }
    }

    // Getters and setters
    public Position getPosition() {
        return position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
