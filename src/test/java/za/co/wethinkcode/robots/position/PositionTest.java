package za.co.wethinkcode.robots.position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {
    private Position position;
    private Position abovePosition;
    private Position topLeft;
    private Position bottomRight;
    private Position outsidePosition;
    private Position samePosition;
    
    @BeforeEach
    void setUp() {
        position = new Position(3, 5);
        abovePosition = new Position(1, 6);
        topLeft = new Position(0, 0);
        bottomRight = new Position(20, 10);
        outsidePosition = new Position(5, 11);
        samePosition = new Position(3, 5);
    }

    @Test
    public void testPositionCoordinates() {
        assertEquals(3, position.getX());
        assertEquals(5, position.getY());

    }

    @Test
    public void testPositionEquality() {
        assertEquals(position, samePosition);
        assertNotEquals(position, abovePosition);
    }

     @Test
    public void testPositionHashCode() {
        assertEquals(position, samePosition);
        assertNotEquals(position, abovePosition);
    }


    @Test
    public void testInBetweenTopLeftBottomRightTrue() {
        assertTrue(position.isIn(topLeft, bottomRight));
    }

    @Test
    public void testInBetweenTopLeftBottomRightFalse() {
        assertFalse(outsidePosition.isIn(topLeft, bottomRight));
    }

    @Test
    public void testInBetweenEdgeCases() {
        assertTrue(topLeft.isIn(topLeft, bottomRight));
        assertTrue(bottomRight.isIn(topLeft, bottomRight));
        assertFalse(new Position(-1, 0).isIn(topLeft, bottomRight));
        assertFalse(new Position(0, -1).isIn(topLeft, bottomRight));
        assertFalse(new Position(21, 10).isIn(topLeft, bottomRight));
        assertFalse(new Position(20, 11).isIn(topLeft, bottomRight));
    }
}
