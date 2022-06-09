package com.swrobotics.lib.swerve;

import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.routine.Routine;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * Manages the modules in a swerve drivebase and calculates odometry.
 */
public class SwerveDrive extends Routine {

    private final SwerveModule[] modules;
    private final Gyroscope gyro;
    private final SwerveDriveKinematics kinematics;
    private final SwerveDriveOdometry odometry;
    private final SwerveModuleSpecs specs;
    
    /**
     * Create a swerve drive to control the passed modules.
     * @param modules An array of modules to control. Each module should already be configured using a SwerveModuleHelper implementation.
     * @param gyro A gyroscope to read the direction of the robot for odometry.
     * @param specs The type of modules that the swerve drive uses. Currently, this only implements Swerve Drive Specialties modules.
     */
    public SwerveDrive(SwerveModule[] modules, Gyroscope gyro, SwerveModuleSpecs specs) {

        this.modules = modules;
        this.gyro = gyro;
        this.specs = specs;

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
        SwerveDriveKinematics.desaturateWheelSpeeds(states, specs.getMaxWheelSpeed());
        
        for (int i = 0; i < modules.length; i++) {
            modules[i].set(states[i]);
        }
    }

    @Override
    public void periodic() {
        SwerveModuleState[] states = new SwerveModuleState[modules.length];
        for (int i = 0; i < modules.length; i++) {
            states[i] = modules[i].getModuleState();
        }
        odometry.update(gyro.getAngle().toRotation2dCW(), states);
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
