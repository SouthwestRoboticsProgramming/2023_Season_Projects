package com.swrobotics.lib.encoder;

import com.swrobotics.lib.math.Angle;

public abstract class AbsoluteEncoder extends Encoder {

    /**
     * Get the relative angle of the sensor.
     * Mark this method as deprecated if this is impossible for the implemented sensor.
     * @return The angle of the sensor relative to where it was when it was initialized.
     */
    public abstract Angle getRelativeAngle();
}
