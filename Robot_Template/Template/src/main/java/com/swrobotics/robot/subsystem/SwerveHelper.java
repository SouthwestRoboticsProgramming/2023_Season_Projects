package com.swrobotics.robot.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;
import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.NewMotor;
import com.swrobotics.lib.motor.CTREMotor;
import com.swrobotics.lib.swerve.SwerveModuleHelper;

public class SwerveHelper implements SwerveModuleHelper{
    private final CTREMotor drive;
    private final CTREMotor steer;
    private final AbsoluteEncoder encoder;
    private final Vec2d position;

    public SwerveHelper(int driveID, int steerID, int encoderID, Vec2d position) {
        this.position = position;

        // Create original motors
        TalonFX driveMotor = new TalonFX(driveID);
        TalonSRX steerMotor = new TalonSRX(steerID);
        CANCoder steerEncoder = new CANCoder(encoderID);

        // Configure motors
        // TODO: Implement



        // Create wrappers
        steer = new CTREMotor(steerMotor);
        drive = new CTREMotor(driveMotor);
        encoder = new AbsoluteEncoder() {

            @Override
            public double getRPM() {
                return steerEncoder.getVelocity() / 360;
            }

            @Override
            public Angle getRawPosition() {
                return Angle.cwDeg(steerEncoder.getAbsolutePosition());
            }
            
        };

        drive.setAbsoluteSensor(encoder);
    }

    @Override
    public NewMotor getDriveMotor() {
        return drive;
    }

    @Override
    public NewMotor getTurnMotor() {
        return steer;
    }

    @Override
    public AbsoluteEncoder getEncoder() {
        return encoder;
    }

    @Override
    public Vec2d getPosition() {
        return position;
    }
}
