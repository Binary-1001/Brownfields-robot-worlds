package za.co.wethinkcode.robots.position;

import java.text.MessageFormat;

public class Position implements PositionInterface {
    private final int X;
    private final int Y;

    public Position(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    @Override
    public int getX() {
        return this.X;
    }

    @Override
    public int getY() {
        return this.Y;
    }

    @Override
    public boolean isIn(Position topLeft, Position bottomRight) {
        boolean withinTop = this.Y >= topLeft.getY();
        boolean withinBottom = this.Y <= bottomRight.getY();
        boolean withinLeft = this.X >= topLeft.getX();
        boolean withinRight = this.X <= bottomRight.getX();
        return withinTop && withinBottom && withinLeft && withinRight;

    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (X != position.X) return false;
        return Y == position.Y;
    }

    @Override
    public String toString() {
        return MessageFormat.format("[{0},{1}]", this.X, this.Y);
    }
}
