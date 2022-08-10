package com.swrobotics.robot;

public final class Constants {

    public static final String CANIVORE = "Gerald";

    public static final double MAX_WHEEL_VELOCITY = 4.11;
    public static final double GEAR_RATIO = 1 / 8.14;
    public static final double WHEEL_RADIUS = 0.05;
    public static final double WHEEL_SPACING = 29.3;


    public static final double MAX_DRIVE_ROTATION = 0.5 * Math.PI;
    public static final double MAX_DRIVE_SPEED = 4.11;

    public static final double INPUT_ACCELERATION = 2.0; // Units per second


    // Intake
    public static final int INTAKE_MOTOR_ID = 3432;
    public static final double INTAKE_ON_PERCENT = 0.5; // Guess

    private Constants() {
        throw new AssertionError();
    }
}
