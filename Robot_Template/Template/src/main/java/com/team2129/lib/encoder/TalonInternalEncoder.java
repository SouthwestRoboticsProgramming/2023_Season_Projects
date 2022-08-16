package com.team2129.lib.encoder;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.team2129.lib.math.Angle;

public class TalonInternalEncoder extends Encoder {

    private final BaseTalon talon;
    private final double ticksPerRotation;

    /**
     * Convert an internal encoder on a Falcon500 or a connected encoder on an SRX into an Enocder type
     * NOTE: Inversion of the sensor should be configured before wrapping the motor.
     * @param talon The motor controller with the sensor attached
     * @param ticksPerRotation The number of sensor ticks that converts into 1 rotation. The direction of this rotatoin
     * determines that direction of clockwise on the sensor.
     */
    public TalonInternalEncoder(BaseTalon talon, double ticksPerRotation) {
        this.talon = talon;
        this.ticksPerRotation = ticksPerRotation;
    }

    @Override
    public Angle getRawAngleImpl() {
        return Angle.ccwDeg(talon.getSelectedSensorPosition() / (ticksPerRotation / 360));
    }

    @Override
    public Angle getVelocityImpl() {
        // 10 is for time conversion from per 100ms to per second
        return Angle.ccwDeg(talon.getSelectedSensorVelocity() / ticksPerRotation * 10);
    }
}
