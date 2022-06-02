package com.swrobotics.lib.gyro;

import com.swrobotics.lib.math.Angle;

public abstract class Gyroscope {

    private Angle offset;
    public Gyroscope() {
        offset = Angle.cwDeg(0);
    }

    public Angle getAngle() {
        return getRawAngle().add(offset);
    }

    public abstract Angle getRawAngle();

    /**
     * Set the position of the gyro.
     * @param angleOffset The angle that the gyro is actually at. 
     * All other measurements will be relative to this.
     */
    public void setOffset(Angle angleOffset) {
        offset.sub(angleOffset);
    }
}
