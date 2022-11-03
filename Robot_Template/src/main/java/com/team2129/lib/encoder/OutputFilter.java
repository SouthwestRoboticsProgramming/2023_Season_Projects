package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

/**
 * Interface to allow the output of an {@link Encoder} to be
 * filtered before it is used.
 */
public interface OutputFilter {
    /**
     * Gets a new angle based on the angle measured by the encoder.
     * 
     * @param angle angle measured by the encoder
     * @return new angle
     */
    Angle filter(Angle angle);
}
