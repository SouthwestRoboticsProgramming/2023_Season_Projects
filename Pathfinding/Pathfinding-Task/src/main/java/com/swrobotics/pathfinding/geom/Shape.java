package com.swrobotics.pathfinding.geom;

public interface Shape {
    boolean collidesWith(RobotShape robot, double robotX, double robotY);
}
