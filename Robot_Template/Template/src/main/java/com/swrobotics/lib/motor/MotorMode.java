package com.swrobotics.lib.motor;

/**
 * Different control modes that are applicable for most motors. <br></br>
 * NOTE: Some of these control modes such as Position, Velocity, Halt, and Hold use closed loop feedback. This means that the motor being
 * used must have some sort of sensor be it internal or external.
 */
public enum MotorMode {
    /**
     * Give the motor a percentage of its received power [0 - 1]
     */
    PERCENT_OUT,

    /**
     * Set the motor to target an angular position in clockwise degrees.
     */
    ANGLE,

    /**
     * Set the motor to target an angular velocity in RPM.
     */
    VELOCITY,

    /**
     * Stop sending power to the motor, letting it coast to a stop.
     */
    STOP, // Let it coast

    /**
     * Halt the motor by actively atempting to maintain an RPM of zero.
     */
    HALT,

    /**
     * Attempt to hold the motors position. It is is moved, it will try to move back to the original position.
     */
    HOLD
}
