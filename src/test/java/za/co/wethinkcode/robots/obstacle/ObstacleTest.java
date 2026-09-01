package za.co.wethinkcode.robots.obstacle;


import org.junit.jupiter.api.Test;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObstacleTest {
    private Position createPositionMock(int x, int y) {
        Position position = mock(Position.class);
        when(position.getX()).thenReturn(x);
        when(position.getY()).thenReturn(y);
        return position;
    }

    @Test
    public void testIsSeeThrough() {
        Mountain mountain = new Mountain(1, 2, 3, 4);
        assertFalse(mountain.isSeeThrough());

        Lake lake = new Lake(createPositionMock(1, 2), createPositionMock(3,4));
        assertTrue(lake.isSeeThrough());

        BottomLessPit bottomLessPit = new BottomLessPit(createPositionMock(1, 3), createPositionMock(5, 7));
        assertTrue(bottomLessPit.isSeeThrough());
    }

    @Test
    public void testCanWalkOver() {
        Mountain mountain = new Mountain(1, 2, 3, 4);
        assertFalse(mountain.canWalkOver());

        Lake lake = new Lake(createPositionMock(1, 2), createPositionMock(3,4));
        assertFalse(lake.canWalkOver());

        BottomLessPit bottomLessPit = new BottomLessPit(createPositionMock(1, 3), createPositionMock(5, 7));
        assertTrue(bottomLessPit.canWalkOver());

    }

    @Test
    public void testInterceptsPointTwo() {
        Position topLeftCorner = this.createPositionMock(3, 4);
        Position bottomRightCorner = this.createPositionMock(8, 9);

        Lake lake = new Lake(topLeftCorner, bottomRightCorner);

        Position pointA = this.createPositionMock(3, 1);
        Position pointB = this.createPositionMock(3, 10);

        assertTrue(lake.interceptsPath(pointA, pointB));

        Position pointC = this.createPositionMock(5, 1);
        Position pointD = this.createPositionMock(5, 7);

        assertTrue(lake.interceptsPath(pointC, pointD));

        Position pointE = this.createPositionMock(13, 7);
        Position pointF = this.createPositionMock(13, 13);

        assertFalse(lake.interceptsPath(pointE, pointF));
    }

    @Test
    public void testCorners() {
        BottomLessPit pit = new BottomLessPit(createPositionMock(10, 14), createPositionMock(18, 23));
        Position topLeftCorner = pit.getTopLeftCorner();
        assertEquals(10, topLeftCorner.getX());
        assertEquals(14, topLeftCorner.getY());

        Position bottomRightCorner = pit.getBottomRightCorner();
        assertEquals(18, bottomRightCorner.getX());
        assertEquals(23, bottomRightCorner.getY());

        Lake lake = new Lake(createPositionMock(13, 14), createPositionMock(26, 35));
        Position lakeTopLeftCorner = lake.getTopLeftCorner();
        assertEquals(13, lakeTopLeftCorner.getX());
        assertEquals(14, lakeTopLeftCorner.getY());

        Position lakeBottomRightCorner = lake.getBottomRightCorner();
        assertEquals(26, lakeBottomRightCorner.getX());
        assertEquals(35, lakeBottomRightCorner.getY());
    }

    @Test
    public void testIsPositionInside() {
        Position topLeftCorner = this.createPositionMock(3, 4);
        Position bottomRightCorner = this.createPositionMock(8, 9);

        Lake lake = new Lake(topLeftCorner, bottomRightCorner);

        Position pointOnTheBoundary = this.createPositionMock(3, 6);
        assertTrue(lake.isPositionInside(pointOnTheBoundary));

        Position pointInside = this.createPositionMock(6, 5);
        assertTrue(lake.isPositionInside(pointInside));

        Position pointOutside = this.createPositionMock(13,16);
        assertFalse(lake.isPositionInside(pointOutside));
    }

    @Test
    public void testInterceptsPoint() {
        Position topLeftCorner = this.createPositionMock(3, 4);
        Position bottomRightCorner = this.createPositionMock(8, 9);

        Lake lake = new Lake(topLeftCorner, bottomRightCorner);

        Position pointA = this.createPositionMock(0, 6);
        Position pointB = this.createPositionMock(11, 6);

        assertTrue(lake.interceptsPath(pointA, pointB));

        Position pointC = this.createPositionMock(5, 8);
        Position pointD = this.createPositionMock(11, 8);

        assertTrue(lake.interceptsPath(pointC, pointD));


        Position pointE = this.createPositionMock(0, 10);
        Position pointF = this.createPositionMock(13, 10);

        assertFalse(lake.interceptsPath(pointE, pointF));
    }

    //iteration 2 test
    @Test
    public void testWalkOver() {
        World world = new World();
        Robot robot = new Robot("Mark", 0, 0, 30);

        world.addRobot(robot);
        List<Robot> robots = world.listRobots();

        assertEquals(1, robots.size());
        assertTrue(robots.contains(robot));

        Position topLeftCorner = new Position(4, 5);
        Position bottomRightCorner = new Position(10, 15);

        BottomLessPit bottomLessPit = new BottomLessPit(topLeftCorner, bottomRightCorner);

        bottomLessPit.walkOver(robot);

        assertFalse(robot.isAlive());

        List<Robot> robots2 = world.listRobots();
        assertEquals(0, robots2.size());
    }
}