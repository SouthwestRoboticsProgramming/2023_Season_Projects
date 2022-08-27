package com.team2129.lib.encoder.filters;

import com.team2129.lib.encoder.OutputFilter;
import com.team2129.lib.math.Angle;

/**
 * A class to combine multiple filters into a compound filter.
 */
public class CompoundFilter implements OutputFilter {
    private final OutputFilter[] filters;

    /**
     * Create a new compound filter from multiple other filters.
     * @param filters Filters to be applied in order
     */
    private CompoundFilter(OutputFilter... filters) {
        this.filters = filters;
    }

    @Override
    public Angle filter(Angle angle) {
        // Apply filters in order
        for (int i = 0; i < filters.length; i++) {
            angle = filters[i].filter(angle);
        }
        return angle;
    }
    
}
