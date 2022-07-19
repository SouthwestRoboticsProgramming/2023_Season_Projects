package com.swrobotics.lib.encoder;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.swrobotics.lib.math.Angle;

/**
 * A class to configure and implement CANCoders.
 */
public class CANCoderImplementation extends AbsoluteEncoder {

    private final static int TIMEOUT_MS = 100;

    private final CANCoder encoder;

    public CANCoderImplementation(int id) {
        encoder = new CANCoder(id);
        encoder.configFactoryDefault();

        CANCoderConfiguration config = new CANCoderConfiguration();
        {
            config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
            config.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
            config.sensorTimeBase = SensorTimeBase.PerSecond;
            config.magnetOffsetDegrees = 0;
        }

        encoder.configAllSettings(config, TIMEOUT_MS);
    }

    /**
     * Get the relative angle of the encoder if it is configured not to use
     * absolute angle by default.
     */
    @Override
    public Angle getRelativeAngle() {
        return Angle.cwDeg(encoder.getPosition());
    }

    @Override
    public Angle getRawAngle() {
        return Angle.cwDeg(encoder.getAbsolutePosition());
    }

    @Override
    public Angle getVelocity() {
        return Angle.cwDeg(encoder.getVelocity());
    }

}
