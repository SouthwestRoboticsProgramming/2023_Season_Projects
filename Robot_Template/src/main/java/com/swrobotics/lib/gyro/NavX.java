package com.swrobotics.lib.gyro;

import com.kauailabs.navx.frc.AHRS;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.CWAngle;

/**
 * Implementation of the navX2 MXP gyroscope manufactured
 * by Kaua'i Labs.
 */
public class NavX extends Gyroscope {
    private final AHRS ahrs;

    /**
     * Creates a new instance using the gyroscope attached to
     * the RoboRIO MXP port.
     */
    public NavX() {
        ahrs = new AHRS();
        ahrs.calibrate();
    }

    @Override
    public Angle getRawAngle() {
        return CWAngle.deg(ahrs.getAngle());
    }
}
