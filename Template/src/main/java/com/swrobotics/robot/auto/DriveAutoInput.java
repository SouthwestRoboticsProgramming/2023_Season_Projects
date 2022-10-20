package com.swrobotics.robot.auto;

import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;

public final class DriveAutoInput {
    private final Vec2d translation;
    private final Angle rotation;
    private final boolean robotRelative;

    public DriveAutoInput(Vec2d translation, Angle rotation, boolean robotRelative) {
        this.translation = translation;
        this.rotation = rotation;
        this.robotRelative = robotRelative;
    }

    public Vec2d getTranslation() {
        return translation;
    }

    public Angle getRotation() {
        return rotation;
    }

    public boolean isRobotRelative() {
        return robotRelative;
    }
}
