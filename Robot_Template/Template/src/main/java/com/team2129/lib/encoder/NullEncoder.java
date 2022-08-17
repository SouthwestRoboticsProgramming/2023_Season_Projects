package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

/**
 * A blank encoder that always returns 0.
 */
public class NullEncoder extends Encoder {

    @Override
    protected Angle getRawAngleImpl() {
        return Angle.zero();
    }

    @Override
    protected Angle getVelocityImpl() {
        return Angle.zero();
    }
    
}
