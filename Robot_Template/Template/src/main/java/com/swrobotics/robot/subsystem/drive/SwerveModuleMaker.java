package com.swrobotics.robot.subsystem.drive;

import com.swrobotics.robot.Constants;
import com.team2129.lib.encoder.CANCoderImplementation;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveModule;

import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.motor.calc.PIDCalculator;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.motor.ctre.TalonSRXMotor;

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

    public static SwerveModule buildModule(Subsystem parent, SwerveModuleDef def, int steerID, Vec2d position, double staticOffsetCWDeg) {
        TalonFXMotor driveMotor = new TalonFXMotor(parent, def.getDriveId(), Constants.CANIVORE);
        driveMotor.setVelocityCalculator(new PIDCalculator(0, 0, 0));
        driveMotor.setInverted(false);
        driveMotor.setNeutralMode(NeutralMode.BRAKE);
        //me,r

        TalonSRXMotor steerMotor = new TalonSRXMotor(parent, steerID);
        steerMotor.setNeutralMode(NeutralMode.BRAKE);
        
        // PIDCalculator steerCalc = new PIDCalculator(TURN_KP, TURN_KI, TURN_KD);
        PIDCalculator steerCalc = new PIDCalculator(0.0003, 0, 0.0);
        steerCalc.enableContinuousInput(-180, 180); // This is required by SwerveModule
        steerMotor.setPositionCalculator(steerCalc);
        steerMotor.setNeutralDeadband(0.005);
        steerMotor.setNominalOutput(0.25);

        CANCoderImplementation canCoder = new CANCoderImplementation(def.getEncoderId(), Constants.CANIVORE);
        canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get() + staticOffsetCWDeg));
        def.getEncoderOffset().onChange(() -> canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get() + staticOffsetCWDeg)));

        return new SwerveModule(driveMotor, steerMotor, canCoder, position, GEAR_RATIO, WHEEL_RADIUS);
    }
}
