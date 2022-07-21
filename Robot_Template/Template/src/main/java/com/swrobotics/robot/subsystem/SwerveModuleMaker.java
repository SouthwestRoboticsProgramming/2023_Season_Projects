package com.swrobotics.robot.subsystem;

import static com.swrobotics.robot.Constants.*;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.swrobotics.lib.encoder.CANCoderImplementation;
import com.swrobotics.lib.motor.TalonMotor;
import com.team2129.lib.drivers.swerve.SwerveModule;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;

import edu.wpi.first.math.controller.PIDController;

/**
 * Configures motors and creates a swerve module
 */
public class SwerveModuleMaker {
    public static SwerveModule buildModule(int driveID, int steerID, int steerEncoderID, Angle steerOffset, Vec2d position) {

        TalonFX driveMotor_toWrap = new TalonFX(driveID);
        driveMotor_toWrap.configFactoryDefault();
        
        TalonFXConfiguration driveConfig = new TalonFXConfiguration();
        {
            SupplyCurrentLimitConfiguration driveSupplyLimit = new SupplyCurrentLimitConfiguration(
                true,
                35, // Continuous current limit
                60, // Peak current limit
                0.1 // Peak current limit duration
            );

            driveConfig.supplyCurrLimit = driveSupplyLimit;
        }

        TalonSRX steerMotor_toWrap = new TalonSRX(steerID);
        steerMotor_toWrap.configFactoryDefault();
        steerMotor_toWrap.setInverted(true);

        TalonMotor driveMotor = new TalonMotor(driveMotor_toWrap);
        driveMotor.setPIDController(new PIDController(DRIVE_KP, DRIVE_KI, DRIVE_KD));

        TalonMotor steerMotor = new TalonMotor(steerMotor_toWrap);

        PIDController steerPID = new PIDController(TURN_KP, TURN_KI, TURN_KD);
        steerPID.enableContinuousInput(-90, 90);
        steerMotor.setPIDController(steerPID);

        driveMotor.assignEncoder(driveMotor.getInternalEncoder(2048));

        CANCoderImplementation canCoder = new CANCoderImplementation(steerEncoderID);
        canCoder.setOffset(steerOffset);
        return new SwerveModule(driveMotor, steerMotor, canCoder, position, GEAR_RATIO, WHEEL_RADIUS);
    }
    
}
