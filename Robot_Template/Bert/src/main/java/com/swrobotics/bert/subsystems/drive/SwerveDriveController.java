package com.swrobotics.bert.subsystems.drive;

import com.kauailabs.navx.frc.AHRS;
import com.swrobotics.bert.control.Input;
import com.swrobotics.bert.subsystems.Subsystem;

import com.swrobotics.bert.util.Utils;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

import static com.swrobotics.bert.constants.DriveConstants.*;

public final class SwerveDriveController implements Subsystem {
    private final AHRS gyro;
    private final SwerveDrive drive;
    private final Input input;
    private ChassisSpeeds chassis;

    private boolean isAutoDrive = false;
    private boolean isAutoTurn = false;
    private boolean isAutoTurnToTarget = false;
    private double autoX = 0;
    private double autoY = 0;
    private double autoTurn = 0;

    private final PIDController autoTurnPID;

    public SwerveDriveController(Input input, AHRS gyro, SwerveDrive drive) {
        this.input = input;
        this.gyro = gyro;
        chassis = new ChassisSpeeds();
        this.drive = drive;

        autoTurnPID = new PIDController(
                BODY_SPIN_KP.get(),
                BODY_SPIN_KI.get(),
                BODY_SPIN_KD.get()
        );
        autoTurnPID.enableContinuousInput(0, 360); // Range to match gyro

        BODY_SPIN_KP.onChange(this::updateBodyPID);
        BODY_SPIN_KI.onChange(this::updateBodyPID);
        BODY_SPIN_KD.onChange(this::updateBodyPID);
    }

    private void updateBodyPID() {
        autoTurnPID.setPID(
                BODY_SPIN_KP.get(),
                BODY_SPIN_KI.get(),
                BODY_SPIN_KD.get()
        );
    }

    @Override
    public void robotPeriodic() {
        
    }

    public void drive(double x, double y) {
        autoX = x;
        autoY = y;
        isAutoDrive = true;
    }

    public void turnToAngle(double angle) {
        autoTurn = Utils.convertAngle0to360(angle);
        isAutoTurn = true;
        isAutoTurnToTarget = true;
        System.out.println("IT IS TURNING TO " + angle);
    }

    public void turn(double amount) {
        autoTurn = amount;
        isAutoTurn = true;
        isAutoTurnToTarget = false;
    }

    @Override
    public void autonomousPeriodic() {
        teleopPeriodic();
    }

    // TODO: REmove
    public Rotation2d getAngleToTarget() {
        double robotX = drive.getOdometryPose().getX();
        double robotY = drive.getOdometryPose().getY();

        double angleRadians = Math.atan2(-robotY, -robotX);

        return new Rotation2d(angleRadians - Math.PI / 2);
    }

    @Override
    public void teleopPeriodic() {
        if (input.getAim()) {
            turnToAngle(getAngleToTarget().getDegrees());
        }

        double driveXControl, driveYControl;
        if (isAutoDrive) {
            driveXControl = autoX;
            driveYControl = autoY;
        } else {
            driveXControl = input.getDriveX();
            driveYControl = input.getDriveY();
        }
        isAutoDrive = false;

        Rotation2d gyroRotation = gyro.getRotation2d();

        double driveRotControl;
        if (isAutoTurn) {
            if (isAutoTurnToTarget) {
                driveRotControl = Utils.clamp(-autoTurnPID.calculate(gyroRotation.getDegrees(), autoTurn), -1, 1);
            } else {
                driveRotControl = autoTurn;
            }
        } else {
            driveRotControl = input.getDriveRot();
        }
        isAutoTurn = false;

        double maxVelocity = MAX_VELOCITY.get();
        double maxTurnVelocity = MAX_TURN_VELOCITY.get();

        double fieldRelativeX = driveYControl * maxVelocity;
        double fieldRelativeY = -driveXControl * maxVelocity;
        double rotation = -driveRotControl * maxTurnVelocity;

        if (input.getSlowMode()) {
            fieldRelativeX *= SLOW_MODE_MULTIPLIER.get();
            fieldRelativeY *= SLOW_MODE_MULTIPLIER.get();
        }

        if (fieldRelativeX == 0 && fieldRelativeY == 0 && rotation == 0) {
            drive.stop();
        } else {
            chassis = ChassisSpeeds.fromFieldRelativeSpeeds(fieldRelativeX, fieldRelativeY, rotation, gyroRotation); // Away velocity, left velocity, counter-clockwise speed, counter-clockwise gyro
            drive.update(chassis);
        }

//        System.out.printf("Gyro: P: %3.3f Y: %3.3f R: %3.3f %n", gyro.getPitch(), gyro.getYaw(), gyro.getRoll());
    }
}
