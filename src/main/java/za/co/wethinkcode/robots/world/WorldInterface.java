package za.co.wethinkcode.robots.world;

import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.robot.Bullet;
import za.co.wethinkcode.robots.robot.MoveResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.view.View;

import java.util.List;

public interface WorldInterface {
    Robot getRobotWithName(String name);

    List<Obstacle> listObstacles();

    List<Robot> listRobots();

    int getWidth();

    int getHeight();

    int getMaxBullets();

    int getMaxShieldStrength();

    Position getTopLeftCorner();

    Position getBottomRightCorner();

    Position getPosition(Robot robot);

    View getFieldOfView(Robot robot);

    Position calculatePosition(Position base, Direction direction, int steps);

    boolean isWithinBounds(Position pos);

    boolean isObstacleAt(Position pos);

    boolean addObstacle(Obstacle obstacle);

    void addRobot(Robot robot);

    Robot createRobot(String name, int max_shield_strength, int max_bullets, int visibility_range);

    boolean isRobotAt(Position pos);

    MoveResponse moveRobot(Robot robot, Position newPosition);

    HitInfo handleBulletFired(Bullet bullet);
}
