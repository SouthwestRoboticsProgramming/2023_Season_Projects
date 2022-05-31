package com.swrobotics.lib.swerve;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.NewMotor;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveDrive { // TODO: Implement subsystem

    private final SwerveModule[] modules;
    private final Gyroscope gyro;
    private final SwerveDriveKinematics kinematics;
    private final SwerveDriveOdometry odometry;
    
    public SwerveDrive(SwerveModule[] modules, Gyroscope gyro) {

        this.modules = modules;
        this.gyro = gyro;

        Translation2d[] positions = new Translation2d[modules.length];
        for (int i = 0; i < modules.length; i++) {
            positions[i] = modules[i].getPosition();
        }

        kinematics = new SwerveDriveKinematics(positions);
        odometry = new SwerveDriveOdometry(kinematics, gyro.getAngle().toRotation2dCCW());
    }

    /**
     * Set the desired movmemnt of the robot
     * @param chassis Desired chassis movement.
     */
    public void setChassis(ChassisSpeeds chassis) {
        SwerveModuleState[] states = kinematics.toSwerveModuleStates(chassis);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, settings.getMaxWheelSpeed());
        
        for (int i = 0; i < modules.length; i++) {
            modules[i].set(states[i]);
        }
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
