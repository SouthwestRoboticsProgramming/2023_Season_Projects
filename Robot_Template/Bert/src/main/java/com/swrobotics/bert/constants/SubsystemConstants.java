package com.swrobotics.bert.constants;

import com.swrobotics.bert.shuffle.ShuffleBoard;
import com.swrobotics.bert.shuffle.TunableBoolean;
import com.swrobotics.bert.shuffle.TuneGroup;

public final class SubsystemConstants {
    private static final TuneGroup SWITCHES = new TuneGroup("Switches", ShuffleBoard.subsystemTab);
        public static final TunableBoolean ENABLE_INPUT              = SWITCHES.getBoolean("Input", true);
        public static final TunableBoolean ENABLE_DRIVE              = SWITCHES.getBoolean("Drive", true);
        public static final TunableBoolean ENABLE_DRIVE_CONTROLLER   = SWITCHES.getBoolean("Drive Controller", true);
        public static final TunableBoolean ENABLE_LIMELIGHT          = SWITCHES.getBoolean("Limelight", true);
        public static final TunableBoolean ENABLE_LOCALIZATION       = SWITCHES.getBoolean("Localization", true);
        public static final TunableBoolean ENABLE_CAMERA_CONTROLLER  = SWITCHES.getBoolean("Camera Controller", true);
        public static final TunableBoolean ENABLE_BALL_DETECTOR      = SWITCHES.getBoolean("Ball Detector", true);
        public static final TunableBoolean ENABLE_HOPPER             = SWITCHES.getBoolean("Hopper", true);
        public static final TunableBoolean ENABLE_FLYWHEEL           = SWITCHES.getBoolean("Flywheel", true);
        public static final TunableBoolean ENABLE_HOOD               = SWITCHES.getBoolean("Hood", true);
        public static final TunableBoolean ENABLE_INTAKE             = SWITCHES.getBoolean("Intake", true);
        public static final TunableBoolean ENABLE_INTAKE_CONTROLLER  = SWITCHES.getBoolean("Intake Controller", true);
        public static final TunableBoolean ENABLE_SHOOTER_CONTROLLER = SWITCHES.getBoolean("Shooter Controller", true);
        public static final TunableBoolean ENABLE_CLIMBER            = SWITCHES.getBoolean("Climber", true);
        public static final TunableBoolean ENABLE_LIGHTS             = SWITCHES.getBoolean("Lights", true);
        public static final TunableBoolean ENABLE_PDP                = SWITCHES.getBoolean("PDP", true);
        public static final TunableBoolean ENABLE_PATHFINDING        = SWITCHES.getBoolean("Pathfinding", true);
        public static final TunableBoolean ENABLE_AUTONOMOUS         = SWITCHES.getBoolean("Autonomous", true);

    private SubsystemConstants() {
        throw new AssertionError();
    }
}
