package com.swrobotics.pathfinding.geom;

public final class CollisionChecks {
    public static boolean checkCircleVsCircleRobot(Circle obj, Circle robot, double robotX, double robotY) {
        double dx = obj.getX() - (robot.getX() + robotX);
        double dy = obj.getY() - (robot.getY() + robotY);

        double radiusTotalSqr = obj.getRadius() + robot.getRadius();
        radiusTotalSqr *= radiusTotalSqr;

        double distanceSqr = dx * dx + dy * dy;

        return distanceSqr <= radiusTotalSqr;
    }
}
