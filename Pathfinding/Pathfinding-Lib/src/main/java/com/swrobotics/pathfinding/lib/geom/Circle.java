package com.swrobotics.pathfinding.lib.geom;

public final class Circle implements RobotShape {
    private double x;
    private double y;
    private double radius;

    public Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean collidesWith(RobotShape robot, double robotX, double robotY) {
        return CollisionChecks.checkCircleVsCircleRobot(this, (Circle) robot, robotX, robotY);
    }
}
