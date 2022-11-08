package com.swrobotics.lib.encoder.filters;

import com.swrobotics.lib.encoder.OutputFilter;
import com.swrobotics.lib.math.Angle;

/**
 * Detects if the output drops to exactly 0 and freezes the output to the last valid output.
 * This is useful for detecting if an encoder is unplugged or loses connection during a
 * match. Unfortunately, motors using a Spark MAX motor controller tend to do this quite often,
 * so it is recommended to use this filter when using a Spark MAX.
 */
public class JumpToZeroFilter implements OutputFilter {
    private Angle lastAngle;

    @Override
    public Angle filter(Angle angle) {
        // Freeze the output if the encoder drops to exactly 0.
        if (lastAngle != null && angle.equals(Angle.ZERO)) {  // TODO: Updated function on main
            return lastAngle;
        }

        lastAngle = angle;
        return angle;
    } 
}
