package com.swrobotics.shufflelog.debug;

import com.swrobotics.shufflelog.util.Vec2d;
import edu.wpi.first.math.geometry.Translation2d;

public class Utils {
    // WPILib coordinate systems:
    // FIELD: X points away from DS, Y points to left from DS
    // ROBOT: X points forward, Y points to left

    // Our coordinate systems:
    // FIELD: Y points away from DS, X points to right from DS
    // ROBOT: Y points forward, X points to right

    // Our robot coords to WPI robot coords
    public static Translation2d toWPIRobotCoords(Vec2d v) {
        return new Translation2d(v.y, -v.x);
    }

    // Our field coords to WPI field coords
    public static Translation2d toWPIFieldCoords(Vec2d v) {
        return new Translation2d(v.y, -v.x);
    }

    // WPI robot coords to our robot coords
    public static Vec2d toRobotCoords(Translation2d tx) {
        return new Vec2d(-tx.getY(), tx.getX());
    }

    // WPI field coords to our field coords
    public static Vec2d toFieldCoords(Translation2d tx) {
        return new Vec2d(-tx.getY(), tx.getX());
    }

    public static double normalize0to360Rad(double angle) {
        angle %= Math.PI * 2;
        if (angle < 0)
            angle += Math.PI * 2;
        return angle;
    }

    // Closest distance along the continuous range from 0 to mod
    public static double continuousDistance(double a, double b, double mod) {
        if (a < 0 || b < 0 || a >= mod || b >= mod)
            throw new IllegalArgumentException("Input out of range");

        double directDist = Math.abs(a - b);
        double wrappedDist = mod - directDist;
        return Math.min(directDist, wrappedDist);
    }
}
