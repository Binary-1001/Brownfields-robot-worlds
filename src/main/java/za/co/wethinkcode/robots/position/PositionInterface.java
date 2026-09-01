package za.co.wethinkcode.robots.position;

public interface PositionInterface {
    int getX();
    int getY();

    boolean isIn(Position topLeft, Position bottomRight);
}
