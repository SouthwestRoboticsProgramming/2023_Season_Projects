package com.swrobotics.lib.encoder;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.swrobotics.lib.math.Angle;

/**
 * A class to configure and implement CANCoders.
 */
public class CANCoderImplementation extends AbsoluteEncoder {
    private final static int TIMEOUT_MS = 100;

    private final CANCoder encoder;

    /**
     * Creates a new instance using a specified CAN ID. This constructor
     * assumes the CANCoder is connected to the RoboRIO CAN bus.
     * 
     * @param id CAN ID of the CANCoder
     */
    public CANCoderImplementation(int id) {
        encoder = new CANCoder(id);
        configEncoder(encoder);
    }

    /**
     * Creates a new instance using a specified CAN ID and bus. The default
     * CAN bus is named {@code "rio"}, as it is the RoboRIO integrated CAN bus.
     * A different CAN bus can be specified to, for example, identify a
     * CANCoder attached through a CANivore.
     * 
     * @param id CAN ID of the CANCoder
     * @param canbus CAN bus the CANCoder is wired to
     */
    public CANCoderImplementation(int id, String canbus) {
        encoder = new CANCoder(id, canbus);
        configEncoder(encoder);
    }
    
    // Sets the CTRE configuration for the CANCoder
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
