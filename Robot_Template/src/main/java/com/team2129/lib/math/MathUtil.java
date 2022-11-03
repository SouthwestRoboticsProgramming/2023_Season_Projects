package com.team2129.lib.math;

/**
 * A class containing commonly used mathematical utilities that
 * are not provided by Java's own libraries.
 */
public final class MathUtil {
    /**
     * Take a value and clamp it to be between specified values.
     * 
     * @param value input value to clamp
     * @param min minimum output value
     * @param max maximum output value
     * @return clamped value
     */
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Take a value and interpolate it between ends of a range.
     * 
     * @param value input value to map
     * @param minOld minimum input value
     * @param maxOld maximum input value
     * @param minNew desired minimum output value
     * @param maxNew desired maximum output value
     * @return mapped value
     */
    public static double map(double value, double minOld, double maxOld, double minNew, double maxNew) {
        return (value - minOld) / (maxOld - minOld) * (maxNew - minNew) + minNew;
    }

    /**
     * Remove noise just around zero by applying a filter.
     * This is equivalent to calling {@link #applyDeadband(double, double, boolean)}
     * with the {@code interpolate} parameter set to true.
     * 
     * @param measurement input value
     * @param band minimum distance from zero to consider as an actual measurement
     * @return value with deadband applied
     */
    public static double applyDeadband(double measurement, double band) {
        return applyDeadband(measurement, band, true);
    }

    /**
     * Remove noise just around zero by applying a filter.
     * 
     * @param measurement input value
     * @param band minimum distance from zero to consider as an actual measurement
     * @param interpolate whether to correct the output to compensate for the 
     *                    "jump" in output at the band threshold
     * @return value with deadband applied
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
