package com.team2129.lib.motor;

import com.team2129.lib.schedule.subsystem.Subsystem;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

/**
 * A wrapper class for CTRE Victor SP motor controllers.
 */
public class VictorSPMotor extends Motor {

    private final VictorSP victor;

    /**
     * Create a VictorSPMotor using an already configured VictorSP.
     * @param victor VictorSP to wrap. NOTE: This should already be configured with inversion etc.
     */
    public VictorSPMotor(Subsystem parent, VictorSP victor) {
        super(parent);
        this.victor = victor;
    }

    @Override
    protected void setPercent(double percent) {
        victor.set(percent);
    }
    
}
