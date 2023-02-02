package com.swrobotics.lib.gyro;

import com.swrobotics.lib.math.Angle;

/**
 * Abstraction for angle gyroscopes.
 */
public abstract class Gyroscope {
    private Angle offset;

    public Gyroscope() {
        offset = Angle.ZERO;
    }

    /**
     * Gets the angle of the gyro with offset applied.
     * 
     * @return adjusted angle of the gyroscope
     */
    public Angle getAngle() {
        return getRawAngle().ccw().add(offset.ccw());
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
        offset = offset.ccw().sub(angleOffset.ccw());
    }

    @Override
    public String toString() {
        return "Angle: " + getAngle() + " Raw: " + getRawAngle();
    }
}
