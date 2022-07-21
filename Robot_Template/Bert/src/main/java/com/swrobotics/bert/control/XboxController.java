package com.swrobotics.bert.control;

public final class XboxController extends Controller {
    private static final int ID_A = 1;
    private static final int ID_B = 2;
    private static final int ID_X = 3;
    private static final int ID_Y = 4;
    private static final int ID_LEFT_SHOULDER = 5;
    private static final int ID_RIGHT_SHOULDER = 6;
    private static final int ID_SELECT = 7;
    private static final int ID_START = 8;
    private static final int ID_LEFT_STICK = 9;
    private static final int ID_RIGHT_STICK = 10;

    private static final int AXIS_LEFT_STICK_X = 0;
    private static final int AXIS_LEFT_STICK_Y = 1;
    private static final int AXIS_LEFT_TRIGGER = 2;
    private static final int AXIS_RIGHT_TRIGGER = 3;
    private static final int AXIS_RIGHT_STICK_X = 4;
    private static final int AXIS_RIGHT_STICK_Y = 5;

    public XboxController(int id) {
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
