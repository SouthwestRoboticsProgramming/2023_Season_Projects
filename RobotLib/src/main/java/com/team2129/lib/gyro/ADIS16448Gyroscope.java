package com.team2129.lib.gyro;

import com.team2129.lib.math.Angle;

import edu.wpi.first.wpilibj.ADIS16448_IMU;

/**
 * Implementation of the ADIS16448 gyroscope manufactured
 * by Analog Devices.
 */
public final class ADIS16448Gyroscope extends Gyroscope {
    private final ADIS16448_IMU imu;

    /**
     * Creates a new instance using the gyroscope attached to
     * the RoboRIO MXP port.
     */
    public ADIS16448Gyroscope() {
        imu = new ADIS16448_IMU();
    }

    @Override
    public Angle getRawAngle() {
        return Angle.cwDeg(imu.getAngle());
    }
}
