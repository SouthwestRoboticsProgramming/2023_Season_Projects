package com.team2129.lib.encoder;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.team2129.lib.math.Angle;

/**
 * A class to configure and implement CANCoders.
 */
public class CANCoderImplementation extends AbsoluteEncoder {

    private final static int TIMEOUT_MS = 100;

    private final CANCoder encoder;

    public CANCoderImplementation(int id) {
        encoder = new CANCoder(id);
        configEncoder(encoder);
    }

    public CANCoderImplementation(int id, String canbus) {
        encoder = new CANCoder(id, canbus);
        configEncoder(encoder);
    }
    
    private void configEncoder(CANCoder encoder) {
        encoder.configFactoryDefault(TIMEOUT_MS);
    
        CANCoderConfiguration config = new CANCoderConfiguration();
        {
            config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
            config.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
            config.sensorTimeBase = SensorTimeBase.PerSecond;
            config.magnetOffsetDegrees = 0;
        }
    
        encoder.configAllSettings(config, TIMEOUT_MS);
        encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, 1);
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
    public Angle getRawAngleImpl() {
        return Angle.cwDeg(encoder.getAbsolutePosition());
    }

    @Override
    public Angle getVelocityImpl() {
        return Angle.cwDeg(encoder.getVelocity());
    }

}
