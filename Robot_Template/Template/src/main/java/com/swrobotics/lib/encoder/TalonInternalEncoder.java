package com.swrobotics.lib.encoder;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.math.Angle;

public class TalonInternalEncoder extends Encoder {

    private final BaseTalon talon;
    private final double ticksPerDegree;

    /**
     * Convert an internal encoder on a Falcon500 or a connected encoder on an SRX into an Enocder type
     * @param talon The motor controller with the sensor attached
     * @param ticksPerDegree The number of sensor ticks that converts into 1 degree. The direction of this degree
     * determines that direction of clockwise on the sensor.
     */
    public TalonInternalEncoder(BaseTalon talon, double ticksPerDegree) {
        this.talon = talon;
        this.ticksPerDegree = ticksPerDegree;
    }

    @Override
    public Angle getRawAngle() {
        return Angle.ccwDeg(talon.getSelectedSensorPosition() / ticksPerDegree);
    }

    @Override
    // TODO: fix this one too
    public Angle getVelocity() {
        return Angle.ccwDeg(talon.getSelectedSensorVelocity() * ticksPerDegree * 10 /* Convert to seconds */);
    }
}
