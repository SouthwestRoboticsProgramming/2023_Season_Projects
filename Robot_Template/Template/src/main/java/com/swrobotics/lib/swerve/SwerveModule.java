package com.swrobotics.lib.swerve;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.MotorMode;
import com.swrobotics.lib.motor.NewMotor;

import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveModule {

    private final NewMotor steer;
    private final NewMotor drive;
    
    public SwerveModule(NewMotor steer, NewMotor drive, AbsoluteEncoder encoder) {
        this.steer = steer;
        this.drive = drive;
        this.drive.setAbsoluteSensor(encoder);
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

    public SwerveModuleState getModuleState() {
        SwerveModuleState state = new SwerveModuleState(drive.getVelocity() / 2 /*FIXME: Change to converstion to m/s*/, steer.getPosition().toRotation2dCCW()); // FIXME: Could be CW instead of CCW
        return state;
    }
}
