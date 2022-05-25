package com.swrobotics.lib.swerve;

import com.swrobotics.lib.math.Vec2d;

import edu.wpi.first.math.geometry.Translation2d;

public class SwerveDriveSettings {
    private Vec2d[] wheelPositions;
    private double maxWheelSpeed;

    public SwerveDriveSettings() {
        // Set default values for if they are not specified
        maxWheelSpeed = 4.11;
    }

    public void setWheelPositions(Vec2d[] positions) {
        wheelPositions = positions;
    }

    public Translation2d[] getWheelPositions() {
        Translation2d[] positions = new Translation2d[wheelPositions.length];
        for (int i=0; i < wheelPositions.length; i++) {
            positions[i] = wheelPositions[i].toTranslation2d();
        }
        return positions;
    }

    public double getMaxWheelSpeed() {
        return maxWheelSpeed;
    }
}
