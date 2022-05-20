package com.swrobotics.lib.motor;

public enum MotorMode {
    PERCENT_OUT,
    POSITION,
    VELOCITY,
    STOP, // Let it coast
    HALT, // Maintain an RPM of zero
    HOLD // Keep the position constant
}
