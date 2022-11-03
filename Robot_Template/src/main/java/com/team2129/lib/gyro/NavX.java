package com.team2129.lib.gyro;

import com.kauailabs.navx.frc.AHRS;
import com.team2129.lib.math.Angle;

public class NavX extends Gyroscope {
    private final AHRS ahrs;

    public NavX() {
        ahrs = new AHRS();
        ahrs.calibrate();
    }

    @Override
    public Angle getRawAngle() {
        return Angle.cwDeg(ahrs.getAngle());
    }
    
}
