package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;

/**
 * Implement this if you want to control your motor's velocity 
 * in a non-standard way.
 */
public interface VelocityCalculator {
    /**
     * Resets the calculator before use.
     * This will be called when switching to the velocity control mode
     * from another mode.
     */
    void reset();

    /**
     * Calculates the percent output of the motor for a target velocity.
     * The percent output will be clamped before use.
     * 
     * @param currentVel current velocity
     * @param targetVel target velocity
     * @return percent output (-1 to 1)
     */
    double calculate(Angle currentVel, Angle targetVel);
}