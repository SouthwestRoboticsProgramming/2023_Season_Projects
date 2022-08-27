package com.swrobotics.robot.subsystem.drive;

import com.swrobotics.robot.Constants;
import com.team2129.lib.encoder.CANCoderImplementation;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveModule;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.motor.calc.PIDCalculator;
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

    public static SwerveModule buildModule(Subsystem parent, SwerveModuleDef def, int steerID, Vec2d position) {
        TalonFXMotor driveMotor = new TalonFXMotor(parent, def.getDriveId(), Constants.CANIVORE);
        driveMotor.setPIDCalculators(DRIVE_KP, DRIVE_KI, DRIVE_KD);
        driveMotor.setInverted(true);

        TalonSRXMotor steerMotor = new TalonSRXMotor(parent, steerID);

        PIDCalculator steerCalc = new PIDCalculator(TURN_KP, TURN_KI, TURN_KD);
        steerCalc.enableContinuousInput(-90, 90);
        steerMotor.setPositionCalculator(steerCalc);

        CANCoderImplementation canCoder = new CANCoderImplementation(def.getEncoderId(), Constants.CANIVORE);
        canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get()));
        def.getEncoderOffset().onChange(() -> canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get())));

        return new SwerveModule(driveMotor, steerMotor, canCoder, position, GEAR_RATIO, WHEEL_RADIUS);
    }
}
