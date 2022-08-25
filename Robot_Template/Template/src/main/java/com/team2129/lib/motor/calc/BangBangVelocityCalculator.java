package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;

import edu.wpi.first.math.controller.BangBangController;

/**
 * Calculates the percent output to set a motor's velocity using a
 * bang-bang controller. 
 */
public final class BangBangVelocityCalculator implements VelocityCalculator {
    private final BangBangController bang;

    /**
     * Creates a new {@code BangBangVelocityCalculator} instance.
     * 
     * From WPILib documentation:<br/>
     * Always ensure that your motor controllers are set to "coast" before
     * attempting to control them with a bang-bang controller.
     */
    public BangBangVelocityCalculator() {
        bang = new BangBangController();
    }

    @Override
    public void reset() {
        // No resetting necessary
    }

    @Override
    public double calculate(Angle currentVel, Angle targetVel) {
        return bang.calculate(currentVel.getCWDeg(), targetVel.getCWDeg());
    }
}
