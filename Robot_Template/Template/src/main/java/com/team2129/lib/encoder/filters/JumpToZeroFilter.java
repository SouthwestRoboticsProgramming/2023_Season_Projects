package com.team2129.lib.encoder.filters;

import com.team2129.lib.encoder.OutputFilter;
import com.team2129.lib.math.Angle;

/**
 * Detects if the output drops to exactly 0 and freezes the output to the last valid output.
 */
public class JumpToZeroFilter implements OutputFilter {
    private Angle lastAngle;

    /**
     * Must be called to update the current value.
     */
    @Override
    public Angle filter(Angle angle) {
        // Freeze the output if the encoder drops to exactly 0.
        if (angle.equals(Angle.zero())) {  // TODO: Updated function on main
            return lastAngle;
        }

        lastAngle = angle;
        return angle;
    }
    
}
