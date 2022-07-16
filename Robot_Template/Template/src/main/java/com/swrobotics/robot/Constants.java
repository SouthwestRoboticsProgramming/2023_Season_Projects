package com.swrobotics.robot;

public final class Constants {

    public static final double DRIVE_KP = 0.0001;
    public static final double DRIVE_KI = 0;
    public static final double DRIVE_KD = 0;
    public static final double TURN_KP = 0.01;
    public static final double TURN_KI = 0;
    public static final double TURN_KD = 0;

    public static final double MAX_WHEEL_VELOCITY = 4.0;
    public static final double GEAR_RATIO = 1 / 8.14;
    public static final double WHEEL_RADIUS = 0.05;
    public static final double WHEEL_SPACING = 29.3;

    private Constants() { // Don't do ' new Constants '
        throw new AssertionError("Don't make an instance of constants, just import static"); // Ignore this
    }
}
