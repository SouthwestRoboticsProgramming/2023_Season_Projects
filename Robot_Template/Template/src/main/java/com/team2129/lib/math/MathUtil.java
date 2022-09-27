package com.team2129.lib.math;

public final class MathUtil {

    /**
     * Take a value and clamp it to be between specified values.
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Take a value and interpolate it between ends of a range.
     * @param value
     * @param minOld
     * @param maxOld
     * @param minNew
     * @param maxNew
     * @return
     */
    public static double map(double value, double minOld, double maxOld, double minNew, double maxNew) {
        return (value - minOld) / (maxOld - minOld) * (maxNew - minNew) + minNew;
    }

    /**
     * Remove noise just around zero by applying a filter.
     * @param measurement
     * @param band
     * @return
     */
    public static double applyDeadband(double measurement, double band) {
        return applyDeadband(measurement, band, false);
    }

    /**
     * Remove noise just around zero by applying a filter.
     * @param measurement
     * @param band
     * @param interpolate
     * @return
     */
    public static double applyDeadband(double measurement, double band, boolean interpolate) {
        if (Math.abs(measurement) < band) {
            return 0;
        }

        if (!interpolate) return measurement;

        if (measurement > 0)
            return map(measurement, band, 1, 0, 1);
        else
            return -map(-measurement, band, 1, 0, 1);
    }

    private MathUtil() {
        throw new AssertionError();
    }
}
