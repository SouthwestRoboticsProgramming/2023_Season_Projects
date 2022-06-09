package com.swrobotics.robot.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;
import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.encoder.CANCoderImplementation;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.Motor;
import com.swrobotics.lib.motor.TalonMotorBuilder;
import com.swrobotics.lib.motor.implementations.TalonMotor;
import com.swrobotics.lib.swerve.SwerveModuleHelper;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class SwerveHelper implements SwerveModuleHelper{
    private final TalonMotor drive;
    private final TalonMotor steer;
    private final AbsoluteEncoder encoder;
    private final Vec2d position;

    /**
     * Example implementation of the SwerveModuleHelper class <br></br>
     * 
     * You should make a similar class to this that configures the motors to your preference.
     *
     * @param driveID
     * @param steerID
     * @param encoderID
     * @param position
     */
    public SwerveHelper(int driveID, int steerID, int encoderID, Vec2d position) {
        this.position = position;

        // Create original motors

        if (steerID == 0 || steerID == 1) {
            VictorSP steerMotor = new VictorSP(steerID);
        } else {
            TalonSRX steerMotor = new TalonSRX(steerID);
        }
        TalonFX driveMotor = new TalonFX(driveID);

        // Configure motors
        // TODO: Implement

        encoder = new CANCoderImplementation(encoderID);


        // Create wrappers
        // steer = new TalonMotorBuilder(steerMotor, encoder)
        //     .build();
        steer = null;
        drive = new TalonMotorBuilder(driveMotor)
            .build();
    }

    @Override
    public Motor getDriveMotor() {
        return drive;
    }

    @Override
    public Motor getTurnMotor() {
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
