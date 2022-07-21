package com.swrobotics.bert.constants;

public final class Constants {
    public static final String CANIVORE = "Gerald";
    public static final int PERIODIC_PER_SECOND = 50;

    public static final int PDP_ID = 62;

    public static final int FX_SENSOR_PER_ROT = 2048;
    // Rotations per minute -> native sensor ticks per 100ms
    public static final double RPM_TO_FX_VELOCITY = FX_SENSOR_PER_ROT / 600.0;

    private Constants() {
        throw new AssertionError();
    }
}
