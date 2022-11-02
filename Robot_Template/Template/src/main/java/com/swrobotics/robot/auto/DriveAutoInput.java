package com.swrobotics.robot.auto;

import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;

public final class DriveAutoInput {
    public enum Mode {
        ROBOT_RELATIVE,
        FIELD_RELATIVE
    }

    private final Vec2d translation;
    private final Angle rotation;
    private final Mode mode;

    public DriveAutoInput(Vec2d translation, Angle rotation, Mode mode) {
        this.translation = translation;
        this.rotation = rotation;
        this.mode = mode;
    }

    public Vec2d getTranslation() {
        return translation;
    }

    public Angle getRotation() {
        return rotation;
    }

    public Mode getMode() {
        return mode;
    }
}
