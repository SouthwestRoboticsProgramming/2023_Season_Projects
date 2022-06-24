package com.swrobotics.lib.swerve2;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.Motor;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public final class SwerveModule {
    private final Motor drive;
    private final Motor turn;
    private final Vec2d centerLocalPosition;
    private final double metersPerSecondToRadiansPerSecond;

    // Turn motor should have the turn encoder set
    public SwerveModule(Motor drive, Motor turn, Vec2d centerLocalPosition, double gearRatio, double wheelRadius) {
        this.drive = drive;
        this.turn = turn;
        this.centerLocalPosition = centerLocalPosition;

        metersPerSecondToRadiansPerSecond = 1.0 / (gearRatio * wheelRadius);
    }

    public void update(SwerveModuleState state) {
        Rotation2d encoderAngleR2D = turn.getEncoder().getAngle().toRotation2dCCW();
        state = SwerveModuleState.optimize(state, encoderAngleR2D);

        // System.out.println("Module is attempting to do: " + state.angle + " " + state.speedMetersPerSecond);
        turn.angle(Angle.ccwRad(state.angle.getRadians()));
        System.out.println(state.speedMetersPerSecond);
        drive.velocity(Angle.cwRad(state.speedMetersPerSecond * metersPerSecondToRadiansPerSecond));
    }

    public Angle getEncoderAngle() {
        return turn.getEncoder().getAngle();
    }

    public Translation2d getWPILibCenterLocalPosition() {
        return new Translation2d(centerLocalPosition.y, -centerLocalPosition.x);
    }
}
