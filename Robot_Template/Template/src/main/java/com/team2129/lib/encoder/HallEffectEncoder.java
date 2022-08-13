package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

public class HallEffectEncoder extends Encoder {

    private final edu.wpi.first.wpilibj.Encoder encoder;
    private final double ticksPerRotation;

    // TODO: Docs
    public HallEffectEncoder(int channelA, int channelB, double ticksPerRotation) {
        encoder = new edu.wpi.first.wpilibj.Encoder(channelA, channelB);
        this.ticksPerRotation = ticksPerRotation;
    }

    @Override
    public Angle getRawAngle() {
        // TODO: Check direction
        return Angle.cwRot(encoder.getDistance() / ticksPerRotation);
    }

    @Override
    public Angle getVelocity() {
        return Angle.cwRot(encoder.getRate());
    }
    
}
