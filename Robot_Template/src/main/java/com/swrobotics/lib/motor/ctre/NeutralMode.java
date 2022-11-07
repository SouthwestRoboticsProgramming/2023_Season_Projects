package com.swrobotics.lib.motor.ctre;

/**
 * The mode in which the motor will behave when its
 * percent output is set to zero.
 */
public enum NeutralMode {
    /**
     * Will try to keep the motor still
     */
    BRAKE,

    /**
     * Will allow the motor to move
     */
    COAST;
}
