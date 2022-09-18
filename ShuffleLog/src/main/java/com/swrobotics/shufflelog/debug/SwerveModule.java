package com.swrobotics.shufflelog.debug;

import com.swrobotics.shufflelog.util.Vec2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import processing.core.PGraphics;

import static com.swrobotics.shufflelog.debug.Utils.*;
import static imgui.ImGui.*;

public class SwerveModule {
    // Assume constant wheel turn speed
    private static final double TURN_SPEED = 2 * Math.PI;
    private static final double DRIVE_VEL_SPEED = 200; // Change in drive velocity per second

    private final Vec2d position;
    private SwerveModuleState targetState;

    private double currentSpeed;
    private double currentAngle;

    public SwerveModule(double x, double y) {
        this.position = new Vec2d(x, y);
        // Assume all wheels start aligned to the right
        // In reality encoder offset & initial value is used here
        currentAngle = 0;
    }

    public void doPeriodic(double delta) {
        double driveSpeed = targetState.speedMetersPerSecond;
        double steerAngle = targetState.angle.getRadians();

        steerAngle %= Math.PI * 2;
        if (steerAngle < 0.0) {
            steerAngle += 2 * Math.PI;
        }

        double difference = steerAngle - currentAngle;
        if (difference >= Math.PI) {
            steerAngle -= 2.0 * Math.PI;
        } else if (difference < -Math.PI) {
            steerAngle += 2.0 * Math.PI;
        }
        difference = steerAngle - currentAngle;

        if (difference > Math.PI / 2.0 || difference < -Math.PI / 2.0) {
            steerAngle += Math.PI;
            driveSpeed *= -1;
        }

        steerAngle %= 2 * Math.PI;
        if (steerAngle < 0.0) {
            steerAngle += 2.0 * Math.PI;
        }

        update(driveSpeed, steerAngle, delta);
    }

    private void update(double driveSpeed, double targetAngle, double delta) {
        // Simulate non-continuous controller
        double diff = driveSpeed - currentSpeed;
        double driveStep = DRIVE_VEL_SPEED * delta;
        if (Math.abs(diff) <= driveStep) {
            currentSpeed = driveSpeed;
        } else {
            currentSpeed += driveStep * Math.signum(diff);
        }

        // Simulate continuous controller from 0 to 2PI
        double twoPi = Math.PI * 2;

        double normTarget = targetAngle % twoPi;
        double normCurrent = currentAngle % twoPi;
        if (normTarget < 0)
            normTarget += twoPi;

        if (normTarget < 0 || normCurrent < 0)
            throw new IllegalStateException();

        double turnStep = delta * TURN_SPEED;
        double rawDiff = normTarget - normCurrent;
        double targetDir = Math.signum(rawDiff);
        double direct = Math.abs(rawDiff);
        double wrapped = twoPi - direct;
        double angleDiff;
        if (direct < wrapped) {
            angleDiff = direct;
        } else {
            angleDiff = wrapped;
            targetDir *= -1;
        }
        if (angleDiff <= turnStep) {
            currentAngle += angleDiff * targetDir;
        } else {
            currentAngle += turnStep * targetDir;
        }

        // Renormalize current angle (CANCoder output range Unsigned_0_to_360 [but in radians])
        currentAngle %= twoPi;
        if (currentAngle < 0)
            currentAngle += twoPi;
    }

    private void drawIndicator(PGraphics g, Vec2d origin, Vec2d direction) {
        g.line(
                (float) origin.x,
                (float) origin.y,
                (float) (origin.x + direction.x),
                (float) (origin.y + direction.y)
        );
    }

    private Vec2d rotation(double angleCCWRad, double mag) {
        double sin = Math.sin(angleCCWRad);
        double cos = Math.cos(angleCCWRad);
        return new Vec2d(cos * mag, sin * mag);
    }

    private Vec2d getTargetAngleVec() {
        Vec2d wpiTargetAngleVec = rotation(targetState.angle.getRadians(), targetState.speedMetersPerSecond);
        return toRobotCoords(new Translation2d(wpiTargetAngleVec.x, wpiTargetAngleVec.y));
    }

    private Vec2d getCurrentAngleVec(double mag) {
        Vec2d wpiCurrentAngleVec = rotation(currentAngle, mag);
        return toRobotCoords(new Translation2d(wpiCurrentAngleVec.x, wpiCurrentAngleVec.y));
    }

    public void draw(PGraphics g, boolean target, boolean wheel, boolean drive) {
        if (target) {
            // Show target state (blue vector)
            g.strokeWeight(2);
            g.stroke(0, 0, 255);
            drawIndicator(g, position, getTargetAngleVec());
        }

        if (wheel) {
            // Wheel (Yellow)
            g.strokeWeight(4);
            g.stroke(255, 255, 0);
            Vec2d curr = getCurrentAngleVec(15);
            g.line(
                    (float) (position.x - curr.x),
                    (float) (position.y - curr.y),
                    (float) (position.x + curr.x),
                    (float) (position.y + curr.y)
            );
        }

        if (drive) {
            // Drive vector (red)
            g.strokeWeight(2);
            g.stroke(255, 0, 0);
            Vec2d curr = getCurrentAngleVec(currentSpeed);
            drawIndicator(g, position, curr);
        }
    }

    public void showGui() {
        text("Position: " + position.x + ", " + position.y);
        text("Target (WPI): " + targetState.angle.getDegrees() + " deg ccw, " + targetState.speedMetersPerSecond + " m/s");
        Vec2d target = getTargetAngleVec();
        text("Target vector: " + target.x + ", " + target.y);
        text("Current angle: " + Math.toDegrees(currentAngle) + " deg ccw");
        text("Current speed: " + currentSpeed);
    }

    public void setTargetState(SwerveModuleState targetState) {
        this.targetState = targetState;
    }

    public SwerveModuleState getCurrentState() {
        return new SwerveModuleState(currentSpeed, new Rotation2d(currentAngle));
    }

    public Vec2d getPosition() {
        return position;
    }
}
