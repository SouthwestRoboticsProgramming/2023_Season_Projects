package com.swrobotics.lib.gyro;

import com.swrobotics.lib.math.Angle;
import edu.wpi.first.wpilibj.ADIS16448_IMU;

public final class ADIS16448Gyroscope extends Gyroscope {
    private final ADIS16448_IMU imu;

    public ADIS16448Gyroscope() {
        imu = new ADIS16448_IMU();
    }

    @Override
    public Angle getRawAngle() {
        // System.out.println(imu.getAngle());
        return Angle.ccwDeg(imu.getAngle());
    }
}
