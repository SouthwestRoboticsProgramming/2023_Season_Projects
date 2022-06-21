package com.swrobotics.lib.encoder;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.swrobotics.lib.math.Angle;

public class CANCoderImplementation extends AbsoluteEncoder {

    private final CANCoder encoder;

    public CANCoderImplementation(int id) {
        encoder = new CANCoder(id);
        encoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);
    }

    // TODO: Actually configure the CANCoder

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

    // TODO: Configuration

}
