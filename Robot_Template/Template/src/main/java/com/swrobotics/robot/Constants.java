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

    public static final double MAX_DRIVE_VELOCITY = 4.11;


    public static final double MAX_DRIVE_ROTATION = 2 * Math.PI;
    public static final double MAX_DRIVE_SPEED = 2.0;

    public static final double INPUT_ACCELERATION = 2.0; // per second

    private Constants() { // Don't do ' new Constants '
        throw new AssertionError(); // Ignore this
    }
}
