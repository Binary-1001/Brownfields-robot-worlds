package za.co.wethinkcode.robots.robot;

import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.position.Position;

public record Bullet(Direction direction, int distance, Position startPosition) {
}
