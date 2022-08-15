package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

public class HallEffectEncoder extends Encoder {

    private final edu.wpi.first.wpilibj.Encoder encoder;
    private final double ticksPerRotation;

    /**
     * A wrapper for the Hall Effect Encoder controlled through the RoboRIO sensor ports.
     * @param channelA
     * @param channelB
     * @param ticksPerRotation
     */
    public HallEffectEncoder(int channelA, int channelB, double ticksPerRotation) {
        encoder = new edu.wpi.first.wpilibj.Encoder(channelA, channelB);
        this.ticksPerRotation = ticksPerRotation;
    }

    @Override
    public Angle getRawAngle() {
        // FIXME: Check direction
        return Angle.cwRot(encoder.getDistance() / ticksPerRotation);
    }

    @Override
    public Angle getVelocity() {
        return Angle.cwRot(encoder.getRate());
    }
    
}
