package com.team2129.lib.utils;

import com.team2129.lib.math.MathUtil;

public class InputUtils {
    // TODO: Move to MathUtils?
    public static double applyDeadband(double measurement, double band) {
        return applyDeadband(measurement, band, false);
    }

    public static double applyDeadband(double measurement, double band, boolean interpolate) {
        if (Math.abs(measurement) < band) {
            return 0;
        }

        if (!interpolate) return measurement;

        if (measurement > 0)
            return MathUtil.map(measurement, band, 1, 0, 1);
        else
            return -MathUtil.map(-measurement, band, 1, 0, 1);
    }
}
