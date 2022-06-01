package com.swrobotics.lib.encoder;

import javax.xml.stream.events.EndDocument;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.swrobotics.lib.math.Angle;

public class CANCoderImplementation extends AbsoluteEncoder {

    private final CANCoder encoder;

    public CANCoderImplementation(int id) {
        encoder = new CANCoder(id);
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

    // TODO: Configuration

}
