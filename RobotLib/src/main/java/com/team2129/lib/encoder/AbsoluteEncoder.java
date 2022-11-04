package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

/**
 * Abstraction for encoders with absolute sensors.
 */
public abstract class AbsoluteEncoder extends Encoder {
    /**
     * Get the relative angle of the sensor. This is necessary because the {@code getAngle()}
     * method defined in {@link Encoder} should return the absolute angle of the sensor.
     * 
     * @return The angle of the sensor relative to where it was when it was initialized.
     */
    public abstract Angle getRelativeAngle();
}
