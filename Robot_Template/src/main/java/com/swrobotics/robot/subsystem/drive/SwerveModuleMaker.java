package com.swrobotics.robot.subsystem.drive;

import com.swrobotics.lib.encoder.CANCoderImplementation;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.CWAngle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.calc.PIDCalculator;
import com.swrobotics.lib.motor.ctre.NeutralMode;
import com.swrobotics.lib.motor.ctre.TalonFXMotor;
import com.swrobotics.lib.motor.ctre.TalonSRXMotor;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.lib.swerve.SwerveModule;
import com.swrobotics.robot.Constants;

/**
 * Configures motors and creates a swerve module
 */
public class SwerveModuleMaker {
    private static final NTDouble DRIVE_KP = new NTDouble("Swerve/Drive/kP", 0.0001);
    private static final NTDouble DRIVE_KI = new NTDouble("Swerve/Drive/kI", 0);
    private static final NTDouble DRIVE_KD = new NTDouble("Swerve/Drive/kD", 0);

    private static final NTDouble TURN_KP = new NTDouble("Swerve/Turn/kP", 0.00003);
    private static final NTDouble TURN_KI = new NTDouble("Swerve/Turn/kI", 0.0);
    private static final NTDouble TURN_KD = new NTDouble("Swerve/Turn/kD", 0.0);

    private static final NTDouble TURN_NOMINAL_OUT = new NTDouble("Swerve/Turn/Nominal Output", 0.25);

    public static final double GEAR_RATIO = 1 / 8.14;
    public static final double WHEEL_RADIUS = 0.05;

    public static SwerveModule buildModule(Subsystem parent, SwerveModuleDef def, int steerID, Vec2d position, double staticOffsetCWDeg) {
        TalonFXMotor driveMotor = new TalonFXMotor(parent, def.getDriveId(), Constants.CANIVORE);
        driveMotor.setVelocityCalculator(new PIDCalculator(DRIVE_KP, DRIVE_KI, DRIVE_KD));
        driveMotor.setInverted(false);
        driveMotor.setNeutralMode(NeutralMode.BRAKE);

        TalonSRXMotor steerMotor = new TalonSRXMotor(parent, steerID);
        steerMotor.setNeutralMode(NeutralMode.BRAKE);
        
        // PIDCalculator steerCalc = new PIDCalculator(TURN_KP, TURN_KI, TURN_KD);
        PIDCalculator steerCalc = new PIDCalculator(TURN_KP, TURN_KI, TURN_KD);
        steerCalc.enableContinuousInput(-180, 180); // This is required by SwerveModule
        steerMotor.setPositionCalculator(steerCalc);
        steerMotor.setNeutralDeadband(0.005);
        steerMotor.setNominalOutput(TURN_NOMINAL_OUT.get());
        TURN_NOMINAL_OUT.onChange(() -> steerMotor.setNominalOutput(TURN_NOMINAL_OUT.get()));

        CANCoderImplementation canCoder = new CANCoderImplementation(def.getEncoderId(), Constants.CANIVORE);
        canCoder.setOffset(CWAngle.deg(def.getEncoderOffset().get() + staticOffsetCWDeg));
        def.getEncoderOffset().onChange(() -> canCoder.setOffset(CWAngle.deg(def.getEncoderOffset().get() + staticOffsetCWDeg)));

        return new SwerveModule(driveMotor, steerMotor, canCoder, position, GEAR_RATIO, WHEEL_RADIUS);
    }
}
