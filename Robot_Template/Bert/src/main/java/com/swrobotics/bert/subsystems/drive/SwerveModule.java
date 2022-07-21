package com.swrobotics.bert.subsystems.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

import com.swrobotics.bert.util.Utils;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

import static com.swrobotics.bert.constants.Constants.*;
import static com.swrobotics.bert.constants.DriveConstants.*;

/**
 * A single swerve module.
 * Controls both wheel rotation and speed
 */
public final class SwerveModule {
    private final TalonFX drive;
    private final TalonSRX turn;
    private final CANCoder canCoder;
    private final PIDController pid;

    public SwerveModule(int driveID, int turnID, int cancoderID, double cancoderOffset) {
        drive = new TalonFX(driveID, CANIVORE);
        turn = new TalonSRX(turnID);
        canCoder = new CANCoder(cancoderID, CANIVORE);

        drive.configFactoryDefault();
        turn.configFactoryDefault();
        
        TalonFXConfiguration driveConfig = new TalonFXConfiguration();
        {
            driveConfig.neutralDeadband = 0.001;
            driveConfig.slot0.kF = DRIVE_KF.get();
            driveConfig.slot0.kP = DRIVE_KP.get();
            driveConfig.slot0.kI = DRIVE_KI.get();
            driveConfig.slot0.kD = DRIVE_KD.get();
            driveConfig.slot0.closedLoopPeakOutput = 1;
            // driveConfig.openloopRamp = 0.5;
            // driveConfig.closedloopRamp = 0.5;
        }
        drive.configAllSettings(driveConfig);
        drive.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

        TalonSRXConfiguration turnConfig = new TalonSRXConfiguration();
        turn.configAllSettings(turnConfig);
        turn.enableVoltageCompensation(false);
        drive.setNeutralMode(NeutralMode.Brake);
        drive.setSelectedSensorPosition(0, 0, 30);
        drive.set(ControlMode.PercentOutput, 0);

        turn.setNeutralMode(NeutralMode.Coast);
        turn.setSelectedSensorPosition(0, 0, 30);
        turn.set(ControlMode.PercentOutput, 0);

        CANCoderConfiguration canConfig = new CANCoderConfiguration();
        canConfig.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        canConfig.magnetOffsetDegrees = cancoderOffset;
        canConfig.sensorDirection = false;

        canCoder.configAllSettings(canConfig);

        pid = new PIDController(TURN_KP.get(), TURN_KI.get(), TURN_KD.get());
        pid.enableContinuousInput(-90, 90);
        pid.setTolerance(20);

        DRIVE_KP.onChange(this::updateDrivePID);
        DRIVE_KI.onChange(this::updateDrivePID);
        DRIVE_KD.onChange(this::updateDrivePID);
        DRIVE_KF.onChange(this::updateDrivePID);

        TURN_KP.onChange(this::updateTurnPID);
        TURN_KI.onChange(this::updateTurnPID);
        TURN_KD.onChange(this::updateTurnPID);
    }

    private void updateDrivePID() {
        drive.config_kP(0, DRIVE_KP.get());
        drive.config_kI(0, DRIVE_KI.get());
        drive.config_kD(0, DRIVE_KD.get());
        drive.config_kF(0, DRIVE_KF.get());
    }

    private void updateTurnPID() {
        pid.setPID(TURN_KP.get(), TURN_KP.get(), TURN_KD.get());
    }

    public SwerveModuleState getRealState() {
        return new SwerveModuleState(drive.getSelectedSensorVelocity() / DRIVE_SPEED_TO_NATIVE_VELOCITY, Rotation2d.fromDegrees(canCoder.getAbsolutePosition()));
    }

    private double fixCurrentAngle(double degreesAngle) {
        if(degreesAngle<90){return degreesAngle;}
        if(degreesAngle>270){return degreesAngle-360;}
        if(degreesAngle<270){return degreesAngle-180;} else {return 0;}
    }

    public double getCANCoderAngle() {
        return canCoder.getAbsolutePosition();
    }

    public boolean isAtTargetAngle() {
        return pid.atSetpoint();
    }

    // Angle incorrectness in degrees
    public double getError() {
        return pid.getPositionError();
    }

    public void stop() {
        turn.set(ControlMode.PercentOutput, 0);
        drive.set(ControlMode.Velocity, 0);
    }
     

    public void update(SwerveModuleState state) {
        Rotation2d canRotation = new Rotation2d(Math.toRadians(canCoder.getAbsolutePosition()));
        Rotation2d currentAngle = new Rotation2d(Math.toRadians(fixCurrentAngle(canCoder.getAbsolutePosition())));
        SwerveModuleState moduleState = SwerveModuleState.optimize(state, canRotation);
        Rotation2d targetAngle = moduleState.angle;

        // Turn to target angle
        double turnAmount = pid.calculate(currentAngle.getDegrees(),targetAngle.getDegrees());
        turnAmount = Utils.clamp(turnAmount,-1.0,1.0);

        // Spin the motors
        turn.set(TalonSRXControlMode.PercentOutput, turnAmount); 
        drive.set(TalonFXControlMode.Velocity, moduleState.speedMetersPerSecond * DRIVE_SPEED_TO_NATIVE_VELOCITY);
    }

    public void resetPID() {
        pid.reset();
    }

}
