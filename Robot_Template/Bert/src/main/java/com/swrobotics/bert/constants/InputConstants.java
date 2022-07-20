package com.swrobotics.bert.constants;

import com.swrobotics.bert.shuffle.ShuffleBoard;
import com.swrobotics.bert.shuffle.TunableDouble;
import com.swrobotics.bert.shuffle.TuneGroup;

public final class InputConstants {
    private static final TuneGroup CONTROLLERS = new TuneGroup("Controllers", ShuffleBoard.inputTab);
    public static final TunableDouble JOYSTICK_DEAD_ZONE = CONTROLLERS.getDouble("Dead Zone", 0.18);

    public static final int DRIVE_CONTROLLER_ID = 0;
    public static final int MANIPULATOR_CONTROLLER_ID = 1;

    private InputConstants() {
        throw new AssertionError();
    }
}
