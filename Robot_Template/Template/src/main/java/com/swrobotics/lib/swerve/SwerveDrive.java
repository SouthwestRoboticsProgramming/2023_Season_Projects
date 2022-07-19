package com.swrobotics.lib.swerve;

import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/*
 * Wheel Layout:
 * 
 * w0 ------- w1
 *  |    ^    |
 *  |    |    |
 *  |    |    |
 * w2 ------- w3
 */

// TODO: BIG: Odometry

/** A class to manage all of the swerve modules in a swerve drive. */
public class SwerveDrive {
    private final Gyroscope gyro;
    private final SwerveModule[] modules;

    private final double maxWheelVelocity;
    private final SwerveDriveKinematics kinematics;

    private Vec2d centerOfRotation;

    public SwerveDrive(Gyroscope gyro, double maxWheelVelocity, SwerveModule... modules) {
        this.gyro = gyro;
        this.modules = modules;

        // Extract positions of modules
        Translation2d[] positions = new Translation2d[modules.length];
        for (int i = 0; i< modules.length; i++) {
            positions[i] = modules[i].getPosition().toTranslation2d();
        }

        kinematics = new SwerveDriveKinematics(positions);
        this.maxWheelVelocity = maxWheelVelocity;

        centerOfRotation = new Vec2d(0, 0);
    }

    /**
     * Set a custom center of rotation away from the center of the robot
     * @param newCenterOfRotation Center of rotation relative to the center of the robot
     */
    public void setCenterOfRotation(Vec2d newCenterOfRotation) {
        centerOfRotation = newCenterOfRotation;
    }

    /**
     * Set the desired motion of the swerve drive
     * @param translation Desired velocity vector in meters per second
     * @param rotationsPerSecond Desired rotational velocity in rotation per second
     */
    public void setMotion(Vec2d translation, Angle rotationsPerSecond) {
        ChassisSpeeds speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            -translation.y, 
            translation.x, // Convert from our coordinates to WPILib
            rotationsPerSecond.getCCWRad(), 
            gyro.getAngle().toRotation2dCCW()
            );

        // speeds = ChassisSpeeds.fromFieldRelativeSpeeds(0.1, 0.1, 0.0, gyro.getAngle().toRotation2dCCW());

        SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds, centerOfRotation.toTranslation2d());
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxWheelVelocity);

        // Update modules
        for (int i = 0; i < states.length; i++) {
            modules[i].setState(states[i]);
        }
    }
}
