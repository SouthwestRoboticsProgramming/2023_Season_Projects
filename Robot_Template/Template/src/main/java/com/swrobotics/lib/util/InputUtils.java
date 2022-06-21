package com.swrobotics.lib.util;

import com.swrobotics.lib.math.MathUtil;

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
