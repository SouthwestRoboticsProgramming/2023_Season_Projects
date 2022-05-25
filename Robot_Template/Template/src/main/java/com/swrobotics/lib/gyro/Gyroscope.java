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

    public void setOffset(Angle angleOffset) {
        offset.sub(angleOffset);
    }
}
