package com.team2129.lib.motor;

import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.wpilibj.simulation.PWMSim;

/**
 * This is a class to temporarily replace a real motor with a simulated PWM output to test
 * that the motors are receiving the correct inputs and producing the correct outputs.
 */
public class DebugMotor extends Motor {

    PWMSim motor;

    public DebugMotor(Subsystem parent, int channel) {
        super(parent);

        motor = new PWMSim(channel);
    }

    public DebugMotor(Subsystem parent, int channel, String canbus) {
        super(parent);

        motor = new PWMSim(channel + 100);
    }

    @Override
    protected void setPercentOutInternal(double percent) {
        motor.setSpeed(percent);
    }
    
}
