package com.swrobotics.lib.gyro;

import com.swrobotics.lib.math.Angle;

/**
 * Abstraction for angle gyroscopes.
 */
public abstract class Gyroscope {
    private Angle offset;

    public Gyroscope() {
        offset = Angle.cwDeg(0);
    }

    /**
     * Gets the angle of the gyro with offset applied.
     * 
     * @return adjusted angle of the gyroscope
     */
    public Angle get() {
        return getRaw().add(offset);
    }

    /**
     * Gets the raw angle of the gyro without any offset applied.
     * 
     * @return raw angle of the gyroscope
     */
    public abstract Angle getRaw();

    /**
     * Sets the position of the gyro to a known value to calibrate it.
     * All other measurements will be relative to this.
     * 
     * @param angleOffset the angle that the gyro is actually at 
     */
    public void set(Angle angleOffset) {
        offset = offset.sub(angleOffset);
    }

    @Override
    public String toString() {
        return "Angle: " + get() + " Raw: " + getRaw();
    }
}
