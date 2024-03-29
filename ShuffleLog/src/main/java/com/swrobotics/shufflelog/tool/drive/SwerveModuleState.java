package com.swrobotics.shufflelog.tool.drive;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.util.CoordinateConversions;
import com.swrobotics.shufflelog.util.Vec2d;
import edu.wpi.first.math.geometry.Translation2d;
import processing.core.PGraphics;

import static imgui.ImGui.*;

public final class SwerveModuleState implements Comparable<SwerveModuleState> {
    private final Vec2d pos;

    private double targetAngle;
    private double currentAngle;
    private double currentVelocity;
    private double targetVelocity;

    public SwerveModuleState(MessageReader reader) {
        double x = reader.readDouble();
        double y = reader.readDouble();
        pos = new Vec2d(x, y);
    }

    public void readState(MessageReader reader) {
        targetAngle = reader.readDouble();
        currentAngle = reader.readDouble();
        targetVelocity = reader.readDouble();
        currentVelocity = reader.readDouble();
    }

    private Vec2d rotation(double angleCCWRad, double mag) {
        double sin = Math.sin(angleCCWRad);
        double cos = Math.cos(angleCCWRad);
        return CoordinateConversions.fromWPICoords(new Translation2d(cos * mag, sin * mag));
    }

    private Vec2d getTargetAngleVec(double magScale) {
        return rotation(targetAngle, targetVelocity * magScale);
    }

    private Vec2d getCurrentAngleVec(double mag) {
        return rotation(currentAngle, mag);
    }

    private void drawIndicator(PGraphics g, Vec2d origin, Vec2d direction) {
        g.line(
                (float) origin.x,
                (float) origin.y,
                (float) (origin.x + direction.x),
                (float) (origin.y + direction.y)
        );
    }

    public void draw(PGraphics g, float strokeMul, float driveScale, boolean target, boolean wheel, boolean drive) {
        if (target) {
            // Show target state (blue vector)
            g.strokeWeight(2 * strokeMul);
            g.stroke(0, 0, 255);
            drawIndicator(g, pos, getTargetAngleVec(driveScale));
        }

        if (wheel) {
            // Wheel (Yellow)
            g.strokeWeight(4 * strokeMul);
            g.stroke(255, 255, 0);
            Vec2d curr = getCurrentAngleVec(15 * strokeMul);
            g.line(
                    (float) (pos.x - curr.x),
                    (float) (pos.y - curr.y),
                    (float) (pos.x + curr.x),
                    (float) (pos.y + curr.y)
            );
        }

        if (drive) {
            // Drive vector (red)
            g.strokeWeight(2 * strokeMul);
            g.stroke(255, 0, 0);
            Vec2d curr = getCurrentAngleVec(currentVelocity * driveScale);
            drawIndicator(g, pos, curr);
        }
    }

    public void showGui() {
        text("Target angle: " + Math.toDegrees(targetAngle) + " ccw deg");
        text("Current angle: " + Math.toDegrees(currentAngle) + " ccw deg");
        text("Target velocity: " + targetVelocity + " m/s");
        text("Current velocity: " + currentVelocity + " m/s");
    }

    public Vec2d getPosition() {
        return pos;
    }

    @Override
    public int compareTo(SwerveModuleState o) {
        return Double.compare(Math.atan2(pos.y, pos.x), Math.atan2(o.pos.y, o.pos.x));
    }
}
