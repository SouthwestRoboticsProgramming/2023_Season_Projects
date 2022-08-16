package com.swrobotics.robot.subsystem.drive;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.swrobotics.robot.Constants;
import com.team2129.lib.encoder.CANCoderImplementation;
import com.team2129.lib.motor.TalonMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.net.NTUtils;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveModule;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;

import edu.wpi.first.math.controller.PIDController;

/**
 * Configures motors and creates a swerve module
 */
public class SwerveModuleMaker {
    private static final NTDouble DRIVE_KP = new NTDouble("Swerve/Drive/kP", 0.0001);
    private static final NTDouble DRIVE_KI = new NTDouble("Swerve/Drive/kI", 0);
    private static final NTDouble DRIVE_KD = new NTDouble("Swerve/Drive/kD", 0);

    private static final NTDouble TURN_KP = new NTDouble("Swerve/Turn/kP", 0.01);
    private static final NTDouble TURN_KI = new NTDouble("Swerve/Turn/kI", 0.0001);
    private static final NTDouble TURN_KD = new NTDouble("Swerve/Turn/kD", 0);

    public static final double GEAR_RATIO = 1 / 8.14;
    public static final double WHEEL_RADIUS = 0.05;

    public static SwerveModule buildModule(Subsystem parent, SwerveModuleDef def, int steerID, Vec2d position) {

        TalonFX driveMotor_toWrap = new TalonFX(def.getDriveId(), Constants.CANIVORE);
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

        driveMotor_toWrap.configAllSettings(driveConfig);

        TalonSRX steerMotor_toWrap = new TalonSRX(steerID);
        steerMotor_toWrap.configFactoryDefault();
        steerMotor_toWrap.setInverted(true);

        TalonMotor driveMotor = new TalonMotor(parent, driveMotor_toWrap);
        driveMotor.setPIDController(NTUtils.makeAutoTunedPID(DRIVE_KP, DRIVE_KI, DRIVE_KD));

        TalonMotor steerMotor = new TalonMotor(parent, steerMotor_toWrap);

        PIDController steerPID = NTUtils.makeAutoTunedPID(TURN_KP, TURN_KI, TURN_KD);
        steerPID.enableContinuousInput(-90, 90);
        steerMotor.setPIDController(steerPID);

        driveMotor.assignEncoder(driveMotor.getInternalEncoder(2048));

        CANCoderImplementation canCoder = new CANCoderImplementation(def.getEncoderId(), Constants.CANIVORE);
        canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get()));
        def.getEncoderOffset().onChange(() -> canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get())));

        return new SwerveModule(driveMotor, steerMotor, canCoder, position, GEAR_RATIO, WHEEL_RADIUS);
    }
}
