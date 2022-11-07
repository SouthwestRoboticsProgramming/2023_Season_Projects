package com.swrobotics.lib.utils;

import com.swrobotics.lib.math.Vec2d;

import edu.wpi.first.math.geometry.Translation2d;

/**
 * This class converts between WPI's coordinate system, as defined on
 * https://first.wpi.edu/wpilib/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/ChassisSpeeds.html#fromFieldRelativeSpeeds(double,double,double,edu.wpi.first.math.geometry.Rotation2d)
 * to our own coordinate system that we believe aligns more with mathematics practices.
 * 
 * 
 * <pre>
 * The WPI coordinate system goes as follows:
 * "Positive x is away from your alliance wall"
 * "Positive y is to your left when standing behind the alliance wall"
 * 
 * The Ultraviolet coordinate system goes as follows:
 * Positive X is to the driver's right.
 * Positive Y is away from the driver.
 */
public final class CoordinateConversions {

    public static Vec2d fromWPICoords(Translation2d tx) {
        return new Vec2d(-tx.getY(), tx.getX());
    }

    public static Translation2d toWPICoords(Vec2d vec) {
        return new Translation2d(vec.y, -vec.x);
    }

    private CoordinateConversions() {
        throw new AssertionError();
    }
}
