package com.swrobotics.lib.swerve;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.MotorMode;
import com.swrobotics.lib.motor.NewMotor;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveModule {

    private final NewMotor steer;
    private final NewMotor drive;
    private final Vec2d position;
    
    public SwerveModule(SwerveModuleHelper helper) {
        drive = helper.getDriveMotor();
        steer = helper.getTurnMotor();
        steer.setAbsoluteSensor(helper.getEncoder());
        position = helper.getPosition();
        // TODO: Gear ratio
    }

    public void set(SwerveModuleState swerveState) {
        double velocity = swerveState.speedMetersPerSecond;
        Angle angle = Angle.cwDeg(swerveState.angle.getDegrees());
        double rpm = 2; // TODO: Use settings given by swerve drive
        drive.set(MotorMode.VELOCITY, velocity);
        steer.set(MotorMode.POSITION, angle.getCWDeg());
    }

    public double getVelocity() {
        return drive.getVelocity();
    }

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
