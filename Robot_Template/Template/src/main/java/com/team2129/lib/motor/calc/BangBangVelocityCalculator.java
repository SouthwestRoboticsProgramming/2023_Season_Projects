package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;

import edu.wpi.first.math.controller.BangBangController;

/**
 * Calculates the percent output to set a motor's velocity using a
 * bang-bang controller. 
 */
public final class BangBangVelocityCalculator implements VelocityCalculator {
    private final BangBangController bang;
    private Angle threshold;
    private double multiplier;

    /**
     * Creates a new {@code BangBangVelocityCalculator} instance.
     * 
     * From WPILib documentation:<br/>
     * Always ensure that your motor controllers are set to "coast" before
     * attempting to control them with a bang-bang controller.
     */
    public BangBangVelocityCalculator() {
        bang = new BangBangController();
        threshold = Angle.zero();
        multiplier = 1.0;
    }

    /**
     * Adjust when the motor should shut off when approaching the setpoint.
     * This can be tuned to create a perfect ramp as deceleration is not instant and with no threshold, the controller will tend to overshoot the setpoint.
     * @param threshold Differance between the current velocity and the target velocity where the motor should shut off.
     * The greater this value, the sooner the motor will shut off.
     */
    public void setThreshold(Angle threshold) {
        this.threshold = threshold;
    }

    /**
     * Adjust the potency of the controller by changing what percent the motor should be set to when the calculator determines that it is below the setpoint.
     * @param multiplier Value the output of the raw bang-bang controller should be multiplied by.
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Get the threshold that determines the distance between the current velocity and the target velocity when the motor should shut off.
     * @return Threshold set by {@code setThreshold}.
     */
    public Angle getThreshold() {
        return threshold;
    }

    /**
     * Get the value that is multiplied by the output of the raw bang-bang controller to adjust what percent out the motor should be set to when determined to be below the setpoint.
     * @return Multpier set by {@code setMultiplier}.
     */
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public void reset() {
        // No resetting necessary
    }

    @Override
    public double calculate(Angle currentVel, Angle targetVel) {
        // Pretend that the target is lower to account for the threshold.
        return bang.calculate(currentVel.getCWDeg(), targetVel.sub(threshold).getCWDeg()) * multiplier;
    }
}
