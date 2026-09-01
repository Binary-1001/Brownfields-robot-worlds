package za.co.wethinkcode.robots.robot;

import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.world.HitInfo;

/**
 * Represents the contract for robot behavior in the game world.
 * Defines actions a robot can perform, such as moving, firing, taking damage, reloading, and repairing,
 * along with methods to query its state and position.
 */
public interface RobotInterface {

    /**
     * Returns the robot's visibility range.
     * 
     * @return the number of units the robot can see
     */
    int getVisibilityRange();

    /**
     * Returns the name of the robot.
     * 
     * @return the robot's name
     */
    String getName();

    /**
     * Returns the robot's current facing direction.
     * 
     * @return the orientation of the robot
     */
    Direction getOrientation();

    /**
     * Returns the robot's current shield strength.
     * 
     * @return shield strength value
     */
    int getShieldStrength();

    /**
     * Returns the number of bullets currently available to the robot.
     * 
     * @return available bullets
     */
    int getBulletsAvail();

    /**
     * Checks if the robot is still operational.
     * 
     * @return true if the robot is alive, false otherwise
     */
    boolean isAlive();

    /**
     * Checks if the robot is currently in repair mode.
     * 
     * @return true if in repair mode, false otherwise
     */
    boolean isRepairMode();

    /**
     * Checks if the robot is currently in reload mode.
     * 
     * @return true if in reload mode, false otherwise
     */
    boolean isReloadMode();

    /**
     * Fires a bullet from the robot. If a robot or obstacle is hit, returns hit details.
     * 
     * @return a HitInfo object containing information about the fired shot
     */
    HitInfo fire();

    /**
     * Inflicts damage to the robot's shield. May cause the robot to die.
     */
    void takeDamage();

    /**
     * Terminates the robot by setting it to a non-operational state.
     */
    void die();

    /**
     * Initiates a repair process to restore the robot's shield strength.
     *
     * @throws InterruptedException if the repair is interrupted
     */
    void repair() throws InterruptedException;

    /**
     * Reloads the robot's ammunition supply.
     *
     * @throws InterruptedException if the reload process is interrupted
     */
    void reload() throws InterruptedException;

    /**
     * Updates the direction the robot is facing.
     * 
     * @param direction the new orientation
     */
    void updateDirection(Direction direction);

    /**
     * Moves the robot a number of steps in the current direction.
     * 
     * @param steps number of steps to move
     * @return the result of the move operation
     */
    MoveResponse move(int steps);

    /**
     * Gets the robot's current position in the world.
     * 
     * @return the current position of the robot
     */
    Position getPosition();
}
