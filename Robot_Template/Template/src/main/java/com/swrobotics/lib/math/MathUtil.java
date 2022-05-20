package com.swrobotics.lib.math;

public final class MathUtil {
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private MathUtil() {
        throw new AssertionError();
    }
}
