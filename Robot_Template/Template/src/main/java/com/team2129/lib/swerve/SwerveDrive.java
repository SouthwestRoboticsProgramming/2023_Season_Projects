package com.team2129.lib.swerve;

import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.gyro.Gyroscope;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/** A class to manage all of the swerve modules in a swerve drive. */
public class SwerveDrive implements Subsystem {
    private final Gyroscope gyro;
    private final SwerveModule[] modules;

    private final double maxWheelVelocity;
    private final SwerveDriveKinematics kinematics;

    private final SwerveDriveOdometry odometry;

    private Vec2d centerOfRotation;

    /**
     * Creates a new {@code SwerveDrive} instance.
     * 
     * @param gyro Gyroscope for angle feedback. Should be zero when facing away from driver station
     * @param maxWheelVelocity Maximum wheel velocity in meters per second
     * @param modules Swerve modules to control
     */
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

        Rotation2d gyroAngle = gyro.getAngle().toRotation2dCW();
        Pose2d initialPose = new Pose2d(0, 0, gyroAngle);
        odometry = new SwerveDriveOdometry(kinematics, gyroAngle, initialPose);

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
     * Set the desired motion of the swerve drive.
     * @param translation Desired velocity vector in meters per second.
     * @param rotationsPerSecond Desired rotational velocity in rotation per second.
     */
    public void setMotion(Vec2d translation, Angle rotationsPerSecond, boolean isFieldRelative) {
        ChassisSpeeds speeds;
        if (isFieldRelative) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
                -translation.y, 
                translation.x, // Convert from our coordinates to WPILib
                rotationsPerSecond.getCCWRad(), 
                gyro.getAngle().toRotation2dCCW()
            );
        } else {
            speeds = new ChassisSpeeds(-translation.y,
                translation.x,
                rotationsPerSecond.getCCWRad()
            );
        }

        setMotion(speeds);
    }
    
    /**
     * Set the desired motion of the swerve drive using a custom ChassisSpeeds object.
     * @param speeds ChassisSpeeds object detailing the desired motion of the swerve drive.
     */
    public void setMotion(ChassisSpeeds speeds) {
        SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds, centerOfRotation.toTranslation2d());
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxWheelVelocity);
    
        // Update modules
        for (int i = 0; i < states.length; i++) {
            modules[i].setState(states[i]);
        }
    }

    public SwerveModule getModule(int index) {
        return modules[index];
    }

    public void setModule(int index, SwerveModule module) {
        modules[index] = module;
    }

    public SwerveDriveKinematics getKinematics() {
        return kinematics;
    }

    public SwerveModuleState[] getStates() {
        SwerveModuleState[] states = new SwerveModuleState[modules.length];

        for (int i = 0; i < modules.length; i++) {
            states[i] = modules[i].getState();
        }

        return states;
    }

    public void printEncoderOffsets() {
        StringBuilder out = new StringBuilder("Encoder offsets (cw deg): ");
        for (SwerveModule module : modules) {
            out.append(String.format("%3.3f", module.getRawAngle().getCWDeg()));
            out.append(" ");
        }
        System.out.println(out);
    }

    // FIXME-Odometry: Check if coordinate space transforms are needed

    /**
     * Get the pose estimated by the odometry
     * @return Estimated pose of the robot
     */
    public Pose2d getOdometryPose() {
        return odometry.getPoseMeters();
    }

    /**
     * Set the current position of the odometry to a known value. The rotation
     * will remain the same.
     * 
     * @param pos Known position of the robot to reset the odometry to.
     */
    public void setOdometryPosition(Vec2d pos) {
        Rotation2d prevRot = getOdometryPose().getRotation();
        setOdometryPose(new Pose2d(pos.x, pos.y, prevRot));
    }

    /**
     * Set the current pose of the odometry to a known value
     * @param pose Known pose of the robot to reset the odometry to.
     */
    public void setOdometryPose(Pose2d pose) {
        odometry.resetPosition(pose, gyro.getAngle().toRotation2dCW());
    }

    @Override
    public void periodic() {
        odometry.update(gyro.getAngle().toRotation2dCW(), getStates());
    }
}
