package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

/**
 * Interface to allow the output of an {@link Encoder} to be
 * filtered before it is used.
 */
public interface OutputFilter {
    Angle filter(Angle angle);
}
