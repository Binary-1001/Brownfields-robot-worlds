package za.co.wethinkcode.robots.world;

import org.junit.jupiter.api.Test;

import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.obstacle.BottomLessPit;
import za.co.wethinkcode.robots.obstacle.Lake;
import za.co.wethinkcode.robots.obstacle.Mountain;
import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.robot.Bullet;
import za.co.wethinkcode.robots.robot.MoveResponse;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.view.ObjectType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WorldTest {
    @Test
    public void testGetRobotWithName() {
        Robot robot1 = mock(Robot.class);
        when(robot1.getName()).thenReturn("June");

        Robot robot2 = mock(Robot.class);
        when(robot2.getName()).thenReturn("July");

        Robot robot3 = mock(Robot.class);
        when(robot3.getName()).thenReturn("Augustine");

        // Create the world and add mocks
        World world = new World();
        world.addRobot(robot1);
        world.addRobot(robot2);
        world.addRobot(robot3);

        assertEquals(robot1, world.getRobotWithName("June"));
        assertEquals(robot3, world.getRobotWithName("Augustine"));
        assertEquals(robot2, world.getRobotWithName("July"));
    }

    @Test
    public void testListRobots() {
        // Create robot mocks
        Robot robot = mock(Robot.class);
        Robot robot1 = mock(Robot.class);
        Robot robot2 = mock(Robot.class);

        // Create world and add mocks
        World world = new World();
        world.addRobot(robot);
        world.addRobot(robot1);
        world.addRobot(robot2);

        List<Robot> robotList = world.listRobots();
        assertEquals(3, robotList.size());
        assertTrue(robotList.contains(robot));
        assertTrue(robotList.contains(robot1));
        assertTrue(robotList.contains(robot2));

        Robot robot3 = mock(Robot.class);
        assertFalse(robotList.contains(robot3));
    }

    private Position createPositionMock(int x, int y) {
        Position position = mock(Position.class);
        when(position.getX()).thenReturn(x);
        when(position.getY()).thenReturn(y);
        return position;
    }

    private Mountain createMountainMock(Position topLeftCorner, Position bottomRightCorner) {
        Mountain mountain = mock(Mountain.class);
        when(mountain.getTopLeftCorner()).thenReturn(topLeftCorner);
        when(mountain.getBottomRightCorner()).thenReturn(bottomRightCorner);
        return mountain;
    }

    private Lake createLakeMock(Position topLeftCorner, Position bottomRightCorner) {
        Lake lake = mock(Lake.class);
        when(lake.getTopLeftCorner()).thenReturn(topLeftCorner);
        when(lake.getBottomRightCorner()).thenReturn(bottomRightCorner);
        return lake;
    }

    private BottomLessPit createBottomLessPitMock(Position topLeftCorner, Position bottomRightCorner) {
        BottomLessPit bottomLessPit = mock(BottomLessPit.class);
        when(bottomLessPit.getTopLeftCorner()).thenReturn(topLeftCorner);
        when(bottomLessPit.getBottomRightCorner()).thenReturn(bottomRightCorner);
        return bottomLessPit;
    }

    @Test
    public void testListObstacles() {
        // Create obstacle mocks
        Mountain mountain = this.createMountainMock(
            this.createPositionMock(3, 3), 
            this.createPositionMock(4, 4));

        Lake lake = this.createLakeMock(
            this.createPositionMock(6, 6), 
            this.createPositionMock(7, 7));

        BottomLessPit bottomLessPit = this.createBottomLessPitMock(
            this.createPositionMock(8, 8), 
            this.createPositionMock(9, 9));


        // Create world and add mocks
        World world = new World();
        world.addObstacle(mountain);
        world.addObstacle(lake);
        world.addObstacle(bottomLessPit);

        List<Obstacle> obstacleList = world.listObstacles();
        assertEquals(3, obstacleList.size());
        assertTrue(obstacleList.contains(mountain));
        assertTrue(obstacleList.contains(lake));
        assertTrue(obstacleList.contains(bottomLessPit));

        Obstacle newObstacle = mock(Obstacle.class);
        assertFalse(obstacleList.contains(newObstacle));
    }

    @Test
    public void testGetTopLeftCorner() {
        World world = new World();
        Position topLeftCorner = world.getTopLeftCorner();
        assertEquals(0, topLeftCorner.getX());
        assertEquals(0, topLeftCorner.getY());
    }


    @Test
    public void testGetBottomRightCorner() {
        World world = new World();
        int height = world.getHeight();
        int width = world.getWidth();

        Position bottomRightCorner = world.getBottomRightCorner();
        assertEquals(width-1, bottomRightCorner.getX());
        assertEquals(height-1, bottomRightCorner.getY());
    }


    @Test
    public void testCreateRobot() {
        World world = new World();

        // Create robot
        world.createRobot("Julie", 5, 3, 10);

        // Get robot
        List<Robot> robotList = world.listRobots();
        assertEquals(1, robotList.size());
    }

    @Test
    public void testMoveRobot() {
        World world = new World();

        Robot robot = new Robot("Keith", 25);
        world.addRobot(robot);

        Position position = world.getPosition(robot);
        Position newPosition;

        if (position.getY() == 0) {
            newPosition = new Position(position.getX(), position.getY()+1);
        } 

        else {
            newPosition = new Position(position.getX(), position.getY()-1);
        }
        
        MoveResponse response = world.moveRobot(robot, newPosition);
        assertEquals(MoveResponse.SUCCESS, response);

        MoveResponse response2 = world.moveRobot(robot, new Position(-1, 0));
        assertEquals(MoveResponse.OUT_OF_RANGE, response2);

        int height = world.getHeight();
        int width = world.getWidth();
        Position outOfRangePosition = new Position(width, height);
        MoveResponse response3 = world.moveRobot(robot, outOfRangePosition);
        assertEquals(MoveResponse.OUT_OF_RANGE, response3);

        // Test for obstruction to be added later. It is still not clear how obstacle works relative to how the world expect it to work.
    }

//    @Test
//    public void testHandleBulletFired() {
//        World world = new World(20, 20, anyInt(), anyInt(), anyInt());
//
//        Obstacle mountain = new Mountain(3, 5, 7, 7);
//        world.addObstacle(mountain);
//
//        Bullet bullet1 = new Bullet(Direction.EAST, 10, new Position(5, 8));
//        HitInfo hitInfo1 = world.handleBulletFired(bullet1);
//
//        assertFalse(hitInfo1.hit());
//
//        Bullet bullet2 = new Bullet(Direction.NORTH, 10, new Position(5, 8));
//        HitInfo hitInfo2 = world.handleBulletFired(bullet2);
//
//        assertFalse(hitInfo2.hit());
//
//        Obstacle lake = new Lake(new Position(10, 7), new Position(15, 13));
//        world.addObstacle(lake);
//
//        Bullet bullet3 = new Bullet(Direction.EAST, 10, new Position(5, 8));
//        HitInfo hitInfo3 = world.handleBulletFired(bullet3);
//
//        assertFalse(hitInfo3.hit());
//    }

    @Test
    public void testHandleBulletFired2() {
        World world = new World();

        Robot robot = new Robot("June", 5, 5, 5);
        world.addRobot(robot);

        Position robotPosition = robot.getPosition();

        Position bulletPosition;
        Direction bulletDirection;
        if (robotPosition.getX() < 2) {
            bulletPosition = new Position(robotPosition.getX()+2, robotPosition.getY());
            bulletDirection = Direction.WEST;
        } 
        else {
            bulletPosition = new Position(robotPosition.getX()-2, robotPosition.getY());
            bulletDirection = Direction.EAST;
        }

        Bullet bullet = new Bullet(bulletDirection, 5, bulletPosition);

        HitInfo hitInfo = world.handleBulletFired(bullet);

        assertTrue(hitInfo.hit());
        assertEquals(2, hitInfo.distance());
        assertEquals(ObjectType.ROBOT, hitInfo.hitObject());
        assertEquals("June", hitInfo.hitObjectName());

        assertEquals(4, robot.getShieldStrength());
    }
}