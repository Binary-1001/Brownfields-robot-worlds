package za.co.wethinkcode.robots.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.obstacle.BottomLessPit;
import za.co.wethinkcode.robots.obstacle.Mine;
import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.robot.Bullet;
import za.co.wethinkcode.robots.robot.MoveResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.view.ObjectType;
import za.co.wethinkcode.robots.view.View;

/**
 * Represents the world in which robots operate. Manages robot positions, obstacles,
 * movement, visibility, and interactions such as shooting.
 */
public class World implements WorldInterface {
    private int WIDTH;
    private int HEIGHT;
    private final int MAX_VISIBILITY_RANGE;
    private final int MAX_BULLETS;
    private final int MAX_SHIELD_STRENGTH;

    private final Map<Obstacle, List<Position>> obstacles = new HashMap<>();
    private final Map<Robot, Position> robots = new HashMap<>();
    private final List<Robot> deadRobots = new ArrayList<>();

    /**
     * Constructs a default 10x10 world with default robot attributes.
     */
    public World() {
        this(10, 10, 2, 2, 2); // Iteration 1 default config
    }

    public World(int size) {
        this(size, size, 2, 2, 2);  // delegates to the main constructor
    }

    public void resizeWorld(int newWidth, int newHeight) {
        this.WIDTH = newWidth;
        this.HEIGHT = newHeight;
    }


    /**
     * Constructs a custom-sized world with configurable attributes.
     *
     * @param width  the world width
     * @param height the world height
     * @param visibilityRange max vision range for robots
     * @param maxBullets max bullets a robot can hold
     * @param maxShieldStrength max shield strength for a robot
     */
    public World(int width, int height, int visibilityRange, int maxBullets, int maxShieldStrength) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.MAX_VISIBILITY_RANGE = visibilityRange;
        this.MAX_BULLETS = maxBullets;
        this.MAX_SHIELD_STRENGTH = maxShieldStrength;
    }

    /**
     * Checks if a robot is marked as dead.
     *
     * @param robotName the name of the robot
     * @return true if the robot is dead
     */
    public boolean isRobotDead(String robotName) {
        for (Robot robot : this.deadRobots) {
            if (robot.getName().equals(robotName)) return true;
        }
        return false;
    }

    /**
     * Marks the specified robot as dead and removes it from the active map.
     *
     * @param robot the robot that has died
     */
    public void reportDeath(Robot robot) {
        this.robots.remove(robot);
        this.deadRobots.add(robot);
    }

    /**
     * Retrieves a robot by name whether alive or dead.
     *
     * @param name the name of the robot
     * @return the matching robot or null
     */
    @Override
    public Robot getRobotWithName(String name) {
    // Check alive robots first (most common case)
    Robot aliveRobot = findRobotByName(this.robots.keySet(), name);
    if (aliveRobot != null) {
        return aliveRobot;
    }
    
    // Check dead robots if not found alive
    return findRobotByName(this.deadRobots, name);
}

private Robot findRobotByName(Collection<Robot> robots, String name) {
    for (Robot robot : robots) {
        if (robot.getName().equals(name)) {
            return robot;
        }
    }
    return null;
}

    /**
     * Returns a list of obstacles in the world.
     *
     * @return the list of obstacles
     */
    @Override
    public List<Obstacle> listObstacles() {
        return new ArrayList<>(obstacles.keySet());
    }

    /**
     * Returns a list of active robots.
     *
     * @return the list of robots
     */
    @Override
    public List<Robot> listRobots() {
        return new ArrayList<>(robots.keySet());
    }

    /**
     * Returns the world width.
     *
     * @return the width
     */
    @Override
    public int getWidth() {
        return this.WIDTH;
    }

    /**
     * Returns the world height.
     *
     * @return the height
     */
    @Override
    public int getHeight() {
        return this.HEIGHT;
    }

    /**
     * Returns the max bullet capacity for robots.
     *
     * @return the max bullets
     */
    @Override
    public int getMaxBullets() {
        return this.MAX_BULLETS;
    }

    /**
     * Returns the max shield strength for robots.
     *
     * @return the max shields
     */
    @Override
    public int getMaxShieldStrength() {
        return this.MAX_SHIELD_STRENGTH;
    }

    /**
     * Returns the configured visibility range.
     *
     * @return the visibility range
     */
    public int getVisibilityRange() {
        return this.MAX_VISIBILITY_RANGE;
    }

    /**
     * Gets the top-left corner of the world.
     *
     * @return the top-left position
     */
    @Override
    public Position getTopLeftCorner() {
        return new Position(0, 0);
    }

    /**
     * Gets the bottom-right corner of the world.
     *
     * @return the bottom-right position
     */
    @Override
    public Position getBottomRightCorner() {
        return new Position(WIDTH - 1, HEIGHT - 1);
    }

    /**
     * Retrieves the position of the specified robot.
     *
     * @param robot the robot whose position is requested
     * @return the robot's position
     */
    @Override
    public Position getPosition(Robot robot) {
        return robots.get(robot);
    }

    /**
     * Computes the field of view for a robot.
     *
     * @param robot the robot
     * @return a view containing visible objects
     */
    @Override
public View getFieldOfView(Robot robot) {
    View view = new View();
    Position origin = robot.getPosition();
    int robotVisibilityRange = robot.getVisibilityRange();

    for (Direction direction : Direction.values()) {
        scanDirection(view, origin, direction, robotVisibilityRange);
    }
    return view;
}

private void scanDirection(View view, Position origin, Direction direction, int range) {
    for (int distance = 1; distance <= range; distance++) {
        Position current = calculatePosition(origin, direction, distance);
        
        if (!isWithinBounds(current)) {
            view.createObject(ObjectType.EDGE, direction, distance - 1);
            return;
        }
        
        if (this.isObstacleAt(current)) {
            Obstacle obstacle = this.getObstacleAt(current);
            view.createObject(ObjectType.OBSTACLE, direction, distance - 1);
            if (!obstacle.isSeeThrough()) return;
            continue; // Skip robot check for see-through obstacles
        }
        
        if (isRobotAt(current)) {
            view.createObject(ObjectType.ROBOT, direction, distance);
            return;
        }
    }
}

    /**
     * Calculates a position in the given direction from a base point.
     *
     * @param base the starting position
     * @param direction the direction to move
     * @param steps the number of steps
     * @return the calculated position
     */
    @Override
    public Position calculatePosition(Position base, Direction direction, int steps) {
        int x = base.getX();
        int y = base.getY();
        if (direction == Direction.NORTH) return new Position(x, y - steps);
        if (direction == Direction.SOUTH) return new Position(x, y + steps);
        if (direction == Direction.EAST) return new Position(x + steps, y);
        return new Position(x - steps, y);
    }

    /**
     * Checks if a position is within the world boundaries.
     *
     * @param pos the position to check
     * @return true if within bounds
     */
    @Override
    public boolean isWithinBounds(Position pos) {
        return pos.getX() >= 0 && pos.getX() < WIDTH && pos.getY() >= 0 && pos.getY() < HEIGHT;
    }

    /**
     * Get an obstacle at a position.
     */
    private Obstacle getObstacleAt(Position pos) {
        for (Obstacle obstacle : obstacles.keySet()) {
            if (obstacle.isPositionInside(pos)) {
                return obstacle;
            }
        }
        return null;
    }

    /**
     * Checks if there is an obstacle at the given position.
     *
     * @param pos the position to check
     * @return true if an obstacle exists at the position
     */
    @Override
    public boolean isObstacleAt(Position pos) {
        for (Obstacle obstacle : obstacles.keySet()) {
            if (obstacle.isPositionInside(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate a random empty position that is unoccupied by robots or obstacles.
     *
     * @return empty position on the world map
     */
    private Position getRandomEmptyPosition() {
        while (true) {
            int randX = (int) Math.floor(Math.random() * this.WIDTH);
            int randY = (int) Math.floor(Math.random() * this.HEIGHT);

            Position position = new Position(randX, randY);
            if (!this.isRobotAt(position) && !this.isObstacleAt(position)) {
                return position;
            }
        }
    }

    /**
     * Adds a new obstacle if the placement is valid.
     *
     * @param obstacle the obstacle to add
     * @return true if successfully placed, false if invalid
     */
    @Override
    public boolean addObstacle(Obstacle obstacle) {
        int startX = obstacle.getTopLeftCorner().getX();
        int startY = obstacle.getTopLeftCorner().getY();

        int endX = obstacle.getBottomRightCorner().getX();
        int endY = obstacle.getBottomRightCorner().getY();

        for (int dx = startX; dx <= endX; dx++) {
            for (int dy = startY; dy <= endY; dy++) {
                Position position = new Position(dx, dy);
                if (this.isObstacleAt(position) || this.isRobotAt(position) || !this.isWithinBounds(position)) {
                    return false;
                }
            }
        }


        List<Position> positions = new ArrayList<>();
        positions.add(obstacle.getTopLeftCorner());
        positions.add(obstacle.getBottomRightCorner());
        this.obstacles.put(obstacle, positions);

        return true;
    }

    /**
     * Adds a robot to the world at a random empty position.
     *
     * @param robot the robot to add
     */
    @Override
    public void addRobot(Robot robot) {
        robots.put(robot, this.getRandomEmptyPosition());
        robot.setWorld(this);
    }

    /**
     * Creates and adds a robot with the given parameters capping values that are over the maximum values of the world.
     *
     * @param name robot name
     * @param robotShieldStrength robot's max shields
     * @param robotBullets robot's bullet capacity
     * @param robotVisibilityRange robot's visibility
     * @return the created robot
     */
    @Override
    public Robot createRobot(String name, int robotShieldStrength, int robotBullets, int robotVisibilityRange) {
        if (robotShieldStrength > MAX_SHIELD_STRENGTH) {//Shield strength exceeds max allowed
            robotShieldStrength = MAX_SHIELD_STRENGTH;
        }
        if (robotBullets > MAX_BULLETS) {//Bullet count exceeds max allowed
            robotBullets = MAX_BULLETS;
        }
        if (robotVisibilityRange > MAX_VISIBILITY_RANGE) {//Visibility range exceeds max allowed
            robotVisibilityRange = MAX_VISIBILITY_RANGE;
        }

        Robot newRobot = new Robot(name, robotShieldStrength, robotBullets, robotVisibilityRange);
        addRobot(newRobot);
        return newRobot;
    }

    /**
     * Checks if a robot is at the given position.
     *
     * @param pos the position to check
     * @return true if a robot is at the position
     */
    @Override
    public boolean isRobotAt(Position pos) {
        for (Robot robot : robots.keySet()) {
            if (robots.get(robot).equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to move a robot to a new position.
     *
     * @param robot the robot to move
     * @param newPosition the destination position
     * @return if the move is successful, obstructed, or out-of-range.
     */
    @Override
public MoveResponse moveRobot(Robot robot, Position newPosition) {
    if (!isWithinBounds(newPosition)) {
        return MoveResponse.OUT_OF_RANGE;
    }

    Position currentPos = robots.get(robot);
    
    // Quick check: if moving to current position
    if (newPosition.equals(currentPos)) {
        return MoveResponse.SUCCESS;
    }

    // Quick check: if another robot is at target position
    if (isRobotAt(newPosition)) {
        return MoveResponse.OBSTRUCTED;
    }

    // Check only the path positions (not the entire rectangle)
    if (!isPathClear(currentPos, newPosition, robot)) {
        return MoveResponse.OBSTRUCTED;
    }

    // Update position
    updateRobotPosition(robot, newPosition);
    return MoveResponse.SUCCESS;
}

private boolean isPathClear(Position from, Position to, Robot robot) {
    int steps = Math.max(
        Math.abs(to.getX() - from.getX()),
        Math.abs(to.getY() - from.getY())
    );

    for (int i = 1; i <= steps; i++) {
        double t = (double) i / steps;
        int x = from.getX() + (int)Math.round(t * (to.getX() - from.getX()));
        int y = from.getY() + (int)Math.round(t * (to.getY() - from.getY()));
        Position checkPos = new Position(x, y);

        if (!isWithinBounds(checkPos)) {
            return false;
        }

        // Skip checking the starting position
        if (checkPos.equals(from)) {
            continue;
        }

        if (isObstacleAt(checkPos)) {
            Obstacle obstacle = getObstacleAt(checkPos);
            if (obstacle.canWalkOver()) {
                BottomLessPit.walkOver(robot);
                removeRobot(robot);
            }
            return false;
        }
    }
    return true;
}

private void updateRobotPosition(Robot robot, Position newPos) {
    // Simple update - just put the new position in the robots map
    robots.put(robot, newPos);
}
    /**
     * Processes a bullet fired in the world.
     *
     * @param bullet the bullet to process
     * @return a HitInfo object detailing the hit result
     */
    @Override
public HitInfo handleBulletFired(Bullet bullet) {
    Position start = bullet.startPosition();
    Direction dir = bullet.direction();
    int maxRange = bullet.distance();

    for (int step = 1; step <= maxRange; step++) {
        Position current = calculatePosition(start, dir, step);

        // Exit early if out of bounds
        if (!isWithinBounds(current)) {
            break;
        }

        // Check robots first
        Robot hitRobot = getRobotAtPosition(current);
        if (hitRobot != null) {
            hitRobot.takeDamage();
            return new HitInfo(true, step, ObjectType.ROBOT, hitRobot.getName());
        }

        // Then check obstacles
        Obstacle obstacle = getObstacleAt(current);
        if (obstacle != null && !obstacle.isSeeThrough()) {
            return new HitInfo(false, 0, null, "");
        }
    }

    return createMissInfo();
}

private Robot getRobotAtPosition(Position position) {
    for (Robot robot : robots.keySet()) {
        if (robot.isAlive() && robots.get(robot).equals(position)) {
            return robot;
        }
    }
    return null;
}
/**
 * Returns a list of all obstacles (excluding mines).
 * Mines are NOT obstacles according to your Mine class documentation.
 *
 * @return list of obstacles (Mountains, Lakes, BottomLessPits)
 */
public List<Obstacle> getObstaclesList() {
    return new ArrayList<>(obstacles.keySet());
}

/**
 * Returns a list of all mines in the world.
 * Note: Currently mines are not being stored separately.
 * You'll need to add a mines collection to store them.
 *
 * @return list of mines
 */
public List<Mine> getMinesList() {
    return new ArrayList<>(mines);
}

// You also need to add this field at the top of your World class with other fields:
private final List<Mine> mines = new ArrayList<>();

// And add this method to place mines in the world:
/**
 * Adds a mine to the world at the specified position.
 *
 * @param mine the mine to add
 * @return true if successfully placed, false if position is occupied
 */
public boolean addMine(Mine mine) {
    Position pos = mine.getPosition();

    // Check if position is valid and unoccupied
    if (!isWithinBounds(pos) || isRobotAt(pos) || isObstacleAt(pos)) {
        return false;
    }

    // Check if there's already a mine at this position
    for (Mine existingMine : mines) {
        if (existingMine.getPosition().equals(pos)) {
            return false;
        }
    }

    mines.add(mine);
    return true;
}

/**
 * Checks if there is a mine at the given position.
 *
 * @param pos the position to check
 * @return true if a mine exists at the position
 */
public boolean isMineAt(Position pos) {
    for (Mine mine : mines) {
        if (mine.getPosition().equals(pos) && mine.isActive()) {
            return true;
        }
    }
    return false;
}

/**
 * Gets the mine at a specific position, if one exists.
 *
 * @param pos the position to check
 * @return the mine at that position, or null if none exists
 */
public Mine getMineAt(Position pos) {
    for (Mine mine : mines) {
        if (mine.getPosition().equals(pos)) {
            return mine;
        }
    }
    return null;
}

    /**
     * Removes all obstacles from the world.
     * Used when restoring a world from the database.
     */
    public void clearObstacles() {
        this.obstacles.clear();
        System.out.println("All obstacles cleared from world.");
    }

    /**
     * Removes all mines from the world.
     * Used when restoring a world from the database.
     */
    public void clearMines() {
        this.mines.clear();
        System.out.println("All mines cleared from world.");
    }

private HitInfo createMissInfo() {
    return new HitInfo(false, 0, null, "");
}
    /**
     * Removes a robot from the world.
     *
     * @param robot the robot to remove
     */
    public void removeRobot(Robot robot) {
        this.robots.remove(robot);
    }

    /**
     * Returns an unmodifiable view of the robot-to-position map.
     *
     * @return the map of robots and their positions
     */
    public Map<Robot, Position> getRobotMap() {
        return Collections.unmodifiableMap(this.robots);
    }

}