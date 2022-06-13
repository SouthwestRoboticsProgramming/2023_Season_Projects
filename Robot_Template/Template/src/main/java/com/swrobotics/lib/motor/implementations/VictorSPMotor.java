package com.swrobotics.lib.motor.implementations;

import com.swrobotics.lib.motor.Motor;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class VictorSPMotor extends Motor {

    private final VictorSP victor;

    public VictorSPMotor(int channel) {
        victor = new VictorSP(channel);
    }

    @Override
    protected void setPercent(double percent) {
        victor.set(percent);
    }
    
}
