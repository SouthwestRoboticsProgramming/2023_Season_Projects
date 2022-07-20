package com.team2129.lib.sensors;

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
     * Get the angle of the gyro with offset applied.
     * @return Adjusted angle of the gyroscope.
     */
    public Angle getAngle() {
        return getRawAngle().add(offset);
    }

    /**
     * Get the raw angle of the gyro without any offset applied.
     * @return Raw angle of the gyroscope.
     */
    public abstract Angle getRawAngle();

    /**
     * Set the position of the gyro.
     * @param angleOffset The angle that the gyro is actually at. 
     * All other measurements will be relative to this.
     */
    public void setOffset(Angle angleOffset) {
        offset.sub(angleOffset);
    }

    @Override
    public String toString() {
        return "Angle: " + getAngle() + " Raw: " + getRawAngle();
    }
}
