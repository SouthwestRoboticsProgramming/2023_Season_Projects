package com.swrobotics.robot.subsystem.drive;

import com.swrobotics.robot.Constants;
import com.team2129.lib.encoder.CANCoderImplementation;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveModule;

import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.motor.calc.EstimateVelocityCalculator;
import com.team2129.lib.motor.calc.PIDCalculator;
import com.team2129.lib.motor.calc.VelocityCalculator;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.motor.ctre.TalonSRXMotor;

/**
 * Configures motors and creates a swerve module
 */
public class SwerveModuleMaker {
    private static final NTDouble DRIVE_MAX_VELOCITY = new NTDouble("Swerve/Drive/Maximum Possible Velocity RPS", 26 * 4.11);

    private static final NTDouble TURN_KP = new NTDouble("Swerve/Turn/kP", 0.0003);
    private static final NTDouble TURN_KI = new NTDouble("Swerve/Turn/kI", 0);
    private static final NTDouble TURN_KD = new NTDouble("Swerve/Turn/kD", 0);

    public static final double GEAR_RATIO = 1 / 8.14;
    public static final double WHEEL_RADIUS = 0.048;

    public static SwerveModule buildModule(Subsystem parent, SwerveModuleDef def, int steerID, Vec2d position, double x) {
        TalonFXMotor driveMotor = new TalonFXMotor(parent, def.getDriveId(), Constants.CANIVORE);
        EstimateVelocityCalculator driveCalculator = new EstimateVelocityCalculator(DRIVE_MAX_VELOCITY);
        //driveMotor.setVelocityCalculator(driveCalculator);
        PIDCalculator driveCalc = new PIDCalculator(0.00005, 0, 0);
        driveMotor.setVelocityCalculator(driveCalc);

        driveMotor.setInverted(true);
        driveMotor.setNeutralMode(NeutralMode.BRAKE);

        TalonSRXMotor steerMotor = new TalonSRXMotor(parent, steerID);
        steerMotor.setNeutralMode(NeutralMode.BRAKE);
        
        // PIDCalculator steerCalc = new PIDCalculator(TURN_KP, TURN_KI, TURN_KD);
        PIDCalculator steerCalc = new PIDCalculator(0.008, 0, 0.0);
        steerCalc.enableContinuousInput(-180, 180); // This is required by SwerveModule
        steerCalc.setTolerance(Angle.zero());
        steerMotor.setPositionCalculator(steerCalc);
        steerMotor.setNeutralDeadband(0.00175);
        steerMotor.setNominalOutput(0.1);

        int staticOffsetCWDeg = 0; //FIXME TOOD Fix ME

        CANCoderImplementation canCoder = new CANCoderImplementation(def.getEncoderId(), Constants.CANIVORE);
        canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get() + staticOffsetCWDeg));
        def.getEncoderOffset().onChange(() -> canCoder.setOffset(Angle.cwDeg(def.getEncoderOffset().get() + staticOffsetCWDeg)));

        return new SwerveModule(driveMotor, steerMotor, canCoder, position, GEAR_RATIO, WHEEL_RADIUS);
    }
}
