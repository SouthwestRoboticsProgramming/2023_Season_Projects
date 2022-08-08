package com.swrobotics.pathfinding.lib.geom;

public interface Shape {
    boolean collidesWith(RobotShape robot, double robotX, double robotY);
}
