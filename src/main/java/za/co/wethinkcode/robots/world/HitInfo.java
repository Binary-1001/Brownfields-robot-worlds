package za.co.wethinkcode.robots.world;

import za.co.wethinkcode.robots.view.ObjectType;

public record HitInfo(boolean hit, int distance, ObjectType hitObject, String hitObjectName) {

}
