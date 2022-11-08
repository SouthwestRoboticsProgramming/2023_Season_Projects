package com.swrobotics.lib.swerve;

import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.MathUtil;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.net.NTDoubleArray;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.lib.utils.CoordinateConversions;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/** 
 * A class to manage all of the swerve modules in a swerve drive.
 */
// FIXME-Odometry: Odometry is almost certainly incorrect here
public class SwerveDrive implements Subsystem {
    private final NTDouble wheelStopTolerance, wheelFullTolerance;
    private final Gyroscope gyro;
    private final SwerveModule[] modules;

    private final double maxWheelVelocity;
    private final SwerveDriveKinematics kinematics;

    private final SwerveDriveOdometry odometry;

    private Translation2d centerOfRotationWpi;

    /**
     * Creates a new {@code SwerveDrive} instance.
     * 
     * @param gyro Gyroscope for angle feedback. Should be zero when facing away from driver station
     * @param maxWheelVelocity Maximum wheel velocity in meters per second
     * @param modules Swerve modules to control
     */
    public SwerveDrive(Gyroscope gyro, double maxWheelVelocity, NTDouble wheelStopTolerance, NTDouble wheelFullTolerance, SwerveModule... modules) {
        this.gyro = gyro;
        this.modules = modules;
        this.wheelStopTolerance = wheelStopTolerance;
        this.wheelFullTolerance = wheelFullTolerance;

        // Extract positions of modules
        Translation2d[] positions = new Translation2d[modules.length];
        for (int i = 0; i< modules.length; i++) {
            positions[i] = CoordinateConversions.toWPICoords(modules[i].getPosition());
        }

        kinematics = new SwerveDriveKinematics(positions);
        this.maxWheelVelocity = maxWheelVelocity;

        // FIXME-Odometry: Not sure if this initialization is correct; I don't have physical robot to test
        Rotation2d gyroAngle = gyro.getAngle().toRotation2dCW();
        Pose2d initialPose = new Pose2d(0, 0, gyroAngle);
        odometry = new SwerveDriveOdometry(kinematics, gyroAngle, initialPose);

        centerOfRotationWpi = CoordinateConversions.toWPICoords(new Vec2d(0, 0));
    }

    /**
     * Set a custom center of rotation away from the center of the robot
     * @param newCenterOfRotation Center of rotation relative to the center of the robot
     */
    public void setCenterOfRotation(Vec2d newCenterOfRotation) {
        centerOfRotationWpi = CoordinateConversions.toWPICoords(newCenterOfRotation);
    }

    /**
     * Set the desired motion of the swerve drive.
     * @param translation Desired velocity vector in meters per second.
     * @param rotationsPerSecond Desired rotational velocity in rotation per second.
     */
    public void setMotion(Vec2d translation, Angle rotationsPerSecond, boolean isFieldRelative) {
        Translation2d wpiTranslation = CoordinateConversions.toWPICoords(translation);

        ChassisSpeeds speeds;
        if (isFieldRelative) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
                wpiTranslation.getX(),
                wpiTranslation.getY(),
                rotationsPerSecond.getCCWRad(), 
                gyro.getAngle().toRotation2dCCW()
            );
        } else {
            speeds = new ChassisSpeeds(
                wpiTranslation.getX(),
                wpiTranslation.getY(),
                rotationsPerSecond.getCCWRad()
            );
        }

        setMotion(speeds);
    }
    
    /**
     * Set the desired motion of the swerve drive using a custom ChassisSpeeds object.
     * @param speeds ChassisSpeeds object detailing the desired motion of the swerve drive.
     */
    private static final NTDouble L_DEBUG = new NTDouble("Swerve/Debug", 0);
    private static final NTDoubleArray L_ERRORS = new NTDoubleArray("Swerve/Errors", 0, 0, 0, 0);
    static { L_DEBUG.setTemporary(); L_ERRORS.setTemporary(); }
    public void setMotion(ChassisSpeeds speeds) {
        SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds, centerOfRotationWpi);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxWheelVelocity);

        double avgErr = 0;
        int k = 0;
        for (SwerveModule module : modules) {
            double err = module.getRotationError().getCCWRad();
            avgErr += err;
            L_ERRORS.set(k++, Math.toDegrees(err));
        }
        avgErr /= modules.length;
        avgErr = Math.toDegrees(avgErr);
        L_DEBUG.set(avgErr);

        double stopTol = wheelStopTolerance.get();
        double fullTol = wheelFullTolerance.get();

        double driveScale = MathUtil.map(avgErr, stopTol, fullTol, 0, 1);
        driveScale = MathUtil.clamp(driveScale, 0, 1);

        // Update modules
        for (int i = 0; i < states.length; i++) {
            states[i].speedMetersPerSecond *= driveScale;
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
