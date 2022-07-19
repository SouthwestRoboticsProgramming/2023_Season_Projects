package com.swrobotics.lib.encoder;

import com.swrobotics.lib.math.Angle;

/**
 * Abstraction for encoders with absolute sensors.
 */
public abstract class AbsoluteEncoder extends Encoder {

    /**
     * Get the relative angle of the sensor.
     * @return The angle of the sensor relative to where it was when it was initialized.
     */
    public abstract Angle getRelativeAngle();
}
