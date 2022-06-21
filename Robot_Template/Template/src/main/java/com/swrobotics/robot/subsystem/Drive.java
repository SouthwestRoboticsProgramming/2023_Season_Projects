package com.swrobotics.robot.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.swrobotics.lib.encoder.CANCoderImplementation;
import com.swrobotics.lib.gyro.ADIS16448Gyroscope;
import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.implementations.TalonMotor;
import com.swrobotics.lib.routine.Routine;
import com.swrobotics.lib.swerve2.SwerveDrive;
import com.swrobotics.lib.swerve2.SwerveModule;
import com.swrobotics.robot.input.Input;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;

import static com.swrobotics.robot.Constants.*; // This lets us just type the variable

public class Drive extends Routine { // 'extends Routine' lets the subsystem do something every robot cycle (all the time)
    private static final double GEAR_RATIO = 1 / 8.14;
    private static final double WHEEL_RADIUS = 0.05;
    private static final double WHEEL_SPACING = 29.3;
    private static final double MAX_WHEEL_SPEED = 4.1;

    private static final double DRIVE_KP = 0.0001;
    private static final double DRIVE_KI = 0;
    private static final double DRIVE_KD = 0;
    private static final double TURN_KP = 0.01;
    private static final double TURN_KI = 0;
    private static final double TURN_KD = 0;

    private final Gyroscope gyro;
    private final SwerveDrive drive;
    private final Input input;

    public Drive(Input input) {
        this.input = input;
        gyro = new ADIS16448Gyroscope();

        double half = WHEEL_SPACING / 2.0;
        drive = new SwerveDrive(
                gyro,
                MAX_WHEEL_SPEED,
                createModule(5, 11, 10, -half,  half, 88.945 + 180 - 90),
                createModule(3, 12,  8,  half,  half, 349.453 - 180 - 90),
                createModule(1, 13,  6, -half, -half, 42.100 - 90),
                createModule(4, 14,  9,  half, -half, 94.746 - 90)
        );
    }

    private SwerveModule createModule(int driveId, int turnId, int canCoderId, double x, double y, double angleOffset) {
        TalonFX drive = new TalonFX(driveId);
        TalonSRX turn = new TalonSRX(turnId);

        drive.configFactoryDefault();
        TalonMotor driveMotor = new TalonMotor(drive);
        driveMotor.setPIDController(new PIDController(DRIVE_KP, DRIVE_KI, DRIVE_KD));
        driveMotor.assignEncoder(driveMotor.getInternalEncoder(1024));

        turn.configFactoryDefault();
        turn.enableVoltageCompensation(false);
        TalonMotor turnMotor = new TalonMotor(turn);
        CANCoderImplementation canCoder = new CANCoderImplementation(canCoderId);
        turnMotor.assignEncoder(canCoder);
        canCoder.setOffset(Angle.cwDeg(angleOffset));
        PIDController turnPID = new PIDController(TURN_KP, TURN_KI, TURN_KD);
        turnPID.enableContinuousInput(-90, 90);
        turnMotor.setPIDController(turnPID);

        return new SwerveModule(
                driveMotor,
                turnMotor,
                new Vec2d(x, y),
                GEAR_RATIO,
                WHEEL_RADIUS
        );
    }

    @Override
    public void periodic() {
        // This runs all the time when the subsystem is enabled

        Vec2d move = input.getDriveTranslation();
        Angle turn = input.getDriveRotation();
        // System.out.println("Moving " + move + ", turning " + turn);

        drive.drive(move, turn);
    }
}
