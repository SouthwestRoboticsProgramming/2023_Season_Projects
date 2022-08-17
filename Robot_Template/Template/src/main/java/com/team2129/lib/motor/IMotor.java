package com.team2129.lib.motor;

import com.team2129.lib.math.Angle;

/**
 * An interface to allow other libraries to use updated motors.
 */
public interface IMotor {
    public void percent(double percent);

    public void velocity(Angle velocity);

    public void position(Angle position);
}
