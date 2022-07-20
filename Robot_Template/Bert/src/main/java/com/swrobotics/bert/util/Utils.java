package com.swrobotics.bert.util;

public class Utils {
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double map(double value, double minOld, double maxOld, double minNew, double maxNew) {
        return (value - minOld) / (maxOld - minOld) * (maxNew - minNew) + minNew;
    }

    public static double normalizeRadians(double angle) {
        return -Math.PI + ((Math.PI * 2 + ((angle + Math.PI) % (Math.PI * 2))) % (Math.PI * 2));
    }

    public static boolean checkTolerance(double value, double tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static double convertAngle0to360(double angle) {
        if (angle >= 0) {
            return angle % 360;
        } else {
            return 360 + (angle % 360);
        }
    }
}
