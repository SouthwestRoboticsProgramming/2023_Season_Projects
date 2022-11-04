package com.team2129.lib.gyro;

import com.team2129.lib.math.Angle;

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
    public Angle getAngle() {
        return getRawAngle().add(offset);
    }

    /**
     * Gets the raw angle of the gyro without any offset applied.
     * 
     * @return raw angle of the gyroscope
     */
    public abstract Angle getRawAngle();

    /**
     * Sets the position of the gyro to a known value to calibrate it.
     * All other measurements will be relative to this.
     * 
     * @param angleOffset the angle that the gyro is actually at 
     */
    public void setOffset(Angle angleOffset) {
        offset = offset.sub(angleOffset);
    }

    @Override
    public String toString() {
        return "Angle: " + getAngle() + " Raw: " + getRawAngle();
    }
}
