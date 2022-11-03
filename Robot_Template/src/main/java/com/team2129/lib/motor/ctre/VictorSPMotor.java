package com.team2129.lib.motor.ctre;

import com.team2129.lib.motor.Motor;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

/**
 * Represents a motor controlled by a Victor SP or Victor SPX
 * over PWM. This motor does not have an internal encoder.
 */
public final class VictorSPMotor extends Motor {
    private final VictorSP motor;

    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * 
     * @param parent parent subsystem
     * @param pwmID PWM port the controller is connected to
     */
    public VictorSPMotor(Subsystem parent, int pwmID) {
        super(parent);
        motor = new VictorSP(pwmID);
    }

    @Override
    public void setPercentOutInternal(double percent) {
        motor.set(percent);
    }
}
