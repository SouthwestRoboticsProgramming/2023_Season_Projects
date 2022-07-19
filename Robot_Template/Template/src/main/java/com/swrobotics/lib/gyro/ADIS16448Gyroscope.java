package com.swrobotics.lib.gyro;

import com.team2129.lib.math.Angle;

import edu.wpi.first.wpilibj.ADIS16448_IMU;

/**
 * Implementation of the Pigeon IMU gyroscope.
 */
public final class ADIS16448Gyroscope extends Gyroscope {
    private final ADIS16448_IMU imu;

    public ADIS16448Gyroscope() {
        imu = new ADIS16448_IMU();
    }

    @Override
    public Angle getRawAngle() {
        return Angle.cwDeg(imu.getAngle());
    }
}
