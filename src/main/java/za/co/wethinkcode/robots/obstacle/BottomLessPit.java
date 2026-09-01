package za.co.wethinkcode.robots.obstacle;

import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.robot.Robot;

/**
 * The BottomLessPit class is a type of Obstacle.
 * It looks like a harmless pit that you can see through and even walk over,
 * but walking over it is deadly — it causes the robot to "die".
 */
public class BottomLessPit extends Obstacle {

    // This constructor sets the area of the pit using two corners.
    public BottomLessPit(Position topLeftCorner, Position bottomRightCorner) {
        super(topLeftCorner, bottomRightCorner);
    }

    /**
     * You can see through the pit, so this returns true.
     */
    @Override
    public boolean isSeeThrough() {
        return true;
    }

    /**
     * The robot can walk over it, so this returns true.
     * However, walking over it has a consequence.
     */
    @Override
    public boolean canWalkOver() {
        return true;
    }

    /**
     * If a robot walks over the pit, it "dies" (is removed or deactivated).
     * 
     * @param robot - the robot that walked over the pit
     */
    public static void walkOver(Robot robot) {
        robot.die(); // This kills the robot.
    }
}

