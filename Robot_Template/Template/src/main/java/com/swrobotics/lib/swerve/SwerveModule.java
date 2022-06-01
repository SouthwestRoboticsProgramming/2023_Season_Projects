package com.swrobotics.lib.swerve;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.MotorMode;
import com.swrobotics.lib.motor.Motor;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * A single swerve module that controls both speed and azimuth (steer).
 */
public class SwerveModule {

    private final Motor steer;
    private final Motor drive;
    private final Vec2d position;

    private double gearRatio; // In X:1

    
    /**
     * Create a new swerve module using a helper class
     * @param helper An implementation of the helper class to hold settings for the module <br></br>
     * NOTE: You will need to write your own implementation of the helper class to configure the motors
     */
    public SwerveModule(SwerveModuleHelper helper) {
        drive = helper.getDriveMotor();
        steer = helper.getTurnMotor();
        steer.assignEncoder(helper.getEncoder());
        position = helper.getPosition();
    }


    /**
     * Set the gear ratio of the module for proper speed calculations.
     * @param gearRatio The ratio of the module in X:1.
     */
    public void setGearRatio(double gearRatio) {
        this.gearRatio = gearRatio;
    }
    

    /**
     * Set the calculated desired state of the swerve module.
     * @param swerveState The state calculated by the kinematics object.
     */
    public void set(SwerveModuleState swerveState) {
        double velocity = swerveState.speedMetersPerSecond;
        Angle angle = Angle.cwDeg(swerveState.angle.getDegrees());
        double rpm = 2 * gearRatio; // TODO: Actually do FIXME
        drive.set(MotorMode.VELOCITY, velocity);
        steer.set(MotorMode.POSITION, angle.getCWDeg());
    }


    /**
     * Get the real velocity of the module.
     * @return Velocity of the module in m/s.
     */
    public double getVelocity() {
        return drive.getVelocity() / gearRatio;
    }

    /**
     * Get the real position of the module for odometry calculation.
     * @return The position of the module relative to the mechanical center of rotation.
     */
    public Translation2d getPosition() {
        return position.toTranslation2d();
    }

    /**
     * Get the real state of the swerve module for use in telemetry and odometry.
     * @return The state the the swerve module is currently at.
     */
    public SwerveModuleState getModuleState() {
        SwerveModuleState state = new SwerveModuleState(drive.getVelocity() / 2 /*FIXME: Change to converstion to m/s*/, steer.getPosition().toRotation2dCCW()); // FIXME: Could be CW instead of CCW
        return state;
    }
}
