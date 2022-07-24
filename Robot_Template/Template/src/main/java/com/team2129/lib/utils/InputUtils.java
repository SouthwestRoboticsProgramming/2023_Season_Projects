package com.team2129.lib.utils;

import com.team2129.lib.math.MathUtil;

public class InputUtils {
    public static double applyDeadband(double measurement, double band) {
        if (Math.abs(measurement) < band) {
            return 0;
        }

        if (measurement > 0)
            return MathUtil.map(measurement, band, 1, 0, 1);
        else
            return -MathUtil.map(-measurement, band, 1, 0, 1);
    }
}
