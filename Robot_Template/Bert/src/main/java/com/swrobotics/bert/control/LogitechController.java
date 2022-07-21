package com.swrobotics.bert.control;

public final class LogitechController extends Controller {
    private static final int ID_A = 2;
    private static final int ID_B = 3;
    private static final int ID_X = 1;
    private static final int ID_Y = 4;
    private static final int ID_LEFT_SHOULDER = 5;
    private static final int ID_RIGHT_SHOULDER = 6;
    private static final int ID_SELECT = 9;
    private static final int ID_START = 10;
    private static final int ID_LEFT_STICK = 11;
    private static final int ID_RIGHT_STICK = 12;

    private static final int AXIS_LEFT_STICK_X = 0;
    private static final int AXIS_LEFT_STICK_Y = 1;
    private static final int AXIS_LEFT_TRIGGER = -1; // Not supported; TODO: map button to axis 0 or 1
    private static final int AXIS_RIGHT_TRIGGER = -1; // Not supported
    private static final int AXIS_RIGHT_STICK_X = 2;
    private static final int AXIS_RIGHT_STICK_Y = 3;

    public LogitechController(int id) {
        super(
            id,
            ID_A,
            ID_B,
            ID_X,
            ID_Y,
            ID_LEFT_SHOULDER,
            ID_RIGHT_SHOULDER,
            ID_SELECT,
            ID_START,
            ID_LEFT_STICK,
            ID_RIGHT_STICK,
            AXIS_LEFT_STICK_X,
            AXIS_LEFT_STICK_Y,
            AXIS_RIGHT_STICK_X,
            AXIS_RIGHT_STICK_Y,
            AXIS_LEFT_TRIGGER,
            AXIS_RIGHT_TRIGGER
        );
    }
}
