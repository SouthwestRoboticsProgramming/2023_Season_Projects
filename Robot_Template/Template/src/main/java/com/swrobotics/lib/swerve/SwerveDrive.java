package com.swrobotics.lib.swerve;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.NewMotor;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveDrive { // TODO: Implement subsystem

    private SwerveModule front_l, front_r, back_l, back_r;
    private SwerveDriveKinematics kinematics;
    private final SwerveDriveSettings settings;
    private final SwerveDriveOdometry odometry;
    
    public SwerveDrive(SwerveDriveSettings settings, NewMotor[] driveMotors, NewMotor[] steerMotors, AbsoluteEncoder[] encoders, Gyroscope gyro) { // TODO: Change motors to settings
        front_l = new SwerveModule(driveMotors[0], steerMotors[0], encoders[0]);
        front_r = new SwerveModule(driveMotors[1], steerMotors[1], encoders[1]);
        back_l = new SwerveModule(driveMotors[2], steerMotors[2], encoders[2]);
        back_r = new SwerveModule(driveMotors[3], steerMotors[3], encoders[3]);

        kinematics = new SwerveDriveKinematics(settings.getWheelPositions());
        odometry = new SwerveDriveOdometry(kinematics, gyro.getAngle().toRotation2dCCW());

        this.settings = settings;

    }

    /**
     * Set the desired movmemnt of the robot
     * @param chassis Desired chassis movement.
     */
    public void setChassis(ChassisSpeeds chassis) {
        SwerveModuleState[] states = kinematics.toSwerveModuleStates(chassis);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, settings.getMaxWheelSpeed());
        front_l.set(states[0]);
        front_r.set(states[1]);
        back_l.set(states[2]);
        back_r.set(states[3]);
    }

    /**
     * Get the estimated position of the robot based on how the wheels were actually moving.
     * NOTE: This can be quite inacurate when the robot is being pushed or when the thread quality is poor.
     * @return Estimated pose of the robot in meters.
     */
    public Vec2d getOdometry() {
        return new Vec2d().fromPose2d(odometry.getPoseMeters());
    }
}
