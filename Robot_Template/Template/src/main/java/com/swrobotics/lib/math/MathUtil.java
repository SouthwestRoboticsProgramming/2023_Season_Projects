package com.swrobotics.lib.math;

public final class MathUtil {
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double map(double value, double minOld, double maxOld, double minNew, double maxNew) {
        return (value - minOld) / (maxOld - minOld) * (maxNew - minNew) + minNew;
    }

    private MathUtil() {
        throw new AssertionError();
    }
}
