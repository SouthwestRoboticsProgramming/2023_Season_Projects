package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

/**
 * Represents a quadrature encoder connected over two DIO ports
 * on the RoboRIO.
 */
public class QuadratureEncoder extends Encoder {
    private final edu.wpi.first.wpilibj.Encoder encoder;
    private final double ticksPerRotation;

    /**
     * Creates a new instance to handle a quadrature encoder on the specified DIO ports.
     * Note: Often the ticks per rotation will be 4 times the amount specified by the
     * manufacturer, as all four edge transitions are counted per physical encoder tick.
     * 
     * @param channelA DIO port the channel A wire is connected to
     * @param channelB DIO port the channel B wire is connected to
     * @param ticksPerRotation number of encoder ticks per full rotation of the input
     */
    public QuadratureEncoder(int channelA, int channelB, double ticksPerRotation) {
        encoder = new edu.wpi.first.wpilibj.Encoder(channelA, channelB);
        this.ticksPerRotation = ticksPerRotation;
    }

    @Override
    public Angle getRawAngleImpl() {
        // FIXME: Check direction
        return Angle.cwRot(encoder.getDistance() / ticksPerRotation);
    }

    @Override
    public Angle getVelocityImpl() {
        return Angle.cwRot(encoder.getRate());
    }
}
