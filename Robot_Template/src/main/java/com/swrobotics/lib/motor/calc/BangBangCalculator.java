package com.swrobotics.lib.motor.calc;

import com.swrobotics.lib.math.Angle;

import edu.wpi.first.math.controller.BangBangController;

/**
 * Calculates the percent output to set a motor's velocity using a
 * bang-bang controller.
 */
public final class BangBangCalculator implements VelocityCalculator, PositionCalculator {
    private final BangBangController bang;
    private Angle threshLow;
    private Angle threshHigh;
    private double multiplier;
    private double minOutput;

    private boolean accelerating;
    private Angle current;
    private Angle target;

    /**
     * Creates a new {@code BangBangCalculator} instance.
     *
     * From WPILib documentation:<p>
     * Always ensure that your motor controllers are set to "coast" before
     * attempting to control them with a bang-bang controller.
     *
     * <p> Ensure that your motor and encoder are reporting in 
     * the same direction before using this controller.
     */
    public BangBangCalculator() {
        bang = new BangBangController();
        multiplier = 1.0;

        threshLow = Angle.ZERO;
        threshHigh = Angle.ZERO;
    }

    /**
     * <pre>
     * Adjust when the motor should shut off when approaching the setpoint.
     *
     * This value is added to the setpoint as a target to aim for when accelerating.
     *
     * This can be tuned to create a perfect ramp as deceleration is not instant and with no threshold, the controller will tend to overshoot the setpoint.
     * </pre>
     * @param threshold Difference between the current velocity and the target velocity where the motor should shut off.
     * The greater this value, the later the motor will shut off.
     */
    public void setUpperThreshold(Angle threshold) {
        threshHigh = threshold;
    }

    /**
     * <pre>
     * Adjust when the motor should shut off when approaching the setpoint.
     * 
     * This value is added to the setpoint as a target to keep above at all times.
     * 
     * This can be tuned to create a perfect ramp as deceleration is not instant and with no threshold, the controller will tend to overshoot the setpoint.
     * </pre>
     * @param threshold Difference between the current velocity and the target velocity that the motor should keep above at all times.
     * The greater this value, the more lenient the motor will be in velocity control.
     */
    public void setLowerThreshold(Angle threshold) {
        threshLow = threshold;
    }

    /**
     * Adjust the potency of the controller by changing what percent the motor should be set to when the calculator determines that it is below the setpoint.
     * @param multiplier Value the output of the raw bang-bang controller should be multiplied by.
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Get how much above the motor should target when accelerating
     * @return
     */
    public Angle getThresholdLow() {
        return threshLow;
    }

    /**
     * Get how far below the setpoint the motor should stay above at all times.
     * @return
     */
    public Angle getThresholdHigh() {
        return threshHigh;
    }

    /**
     * Get the value that is multiplied by the output of the raw bang-bang controller to adjust what percent out the motor should be set to when determined to be below the setpoint.
     * @return Multpier set by {@code setMultiplier}.
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Set the value the calculator should output when
     * it reads above the setpoint
     * @param minOutput Minimum value the calculator will output
     */
    public void setMinOutput(double minOutput) {
        this.minOutput = minOutput;
    }

    /**
     * Get the lowest output as defined by {@code setMinOutput()}.
     */
    public double getMinOutput() {
        return minOutput;
    }

    public boolean inTolerance() {
        if (current == null || target == null) return false;

        boolean aboveMin = Math.abs(current.cw().deg()) > Math.abs(target.cw().deg() + threshLow.cw().deg());
        boolean belowMax = Math.abs(current.cw().deg()) < Math.abs(target.cw().deg() + threshHigh.cw().deg());
        return aboveMin && belowMax;
    }

    @Override
    public void reset() {
        // No resetting necessary
    }

    @Override
    public double calculate(Angle currentReading, Angle targetReading) {
        current = currentReading;
        target = targetReading;

        // Define targets using thresholds
        Angle targetLow = targetReading;
        Angle targetHigh = targetReading;
        
        // targetLow = targetLow.sub(threshLow);
        targetLow = targetReading.cw().add(threshLow.cw());
        targetHigh = targetReading.cw().add(threshHigh.cw());

        // Determine if the velocity should be increasing or decreasing
        if (currentReading.cw().deg() < targetLow.cw().deg()) accelerating = true; // Velocity is below both targets
        if (currentReading.cw().deg() > targetHigh.cw().deg()) accelerating = false; // Velocity is above both targets

        // If the velocity is between the targets, the bang bang controller will continue to target the same target as before.
        double target;
        if (accelerating) {
            target = targetHigh.cw().deg();
        } else {
            target = targetLow.cw().deg();
        }

        double bangOut = bang.calculate(currentReading.cw().deg(), target);
        // System.out.println("Target: " + target + " current: " + currentReading.getCWDeg());
        
        if (Math.abs(minOutput) > Math.abs(bangOut * multiplier)) {
            bangOut = minOutput;
        } else {
            bangOut *= multiplier;
        }
        return bangOut * multiplier;
    }
}
