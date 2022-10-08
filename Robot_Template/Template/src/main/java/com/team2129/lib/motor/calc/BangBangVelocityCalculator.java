package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;

import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Calculates the percent output to set a motor's velocity using a
 * bang-bang controller.
 */
public final class BangBangVelocityCalculator implements VelocityCalculator {
    private final BangBangController bang;
    private SlewRateLimiter rateLimiter;
    private double rampSeconds;
    private Angle threshLow;
    private Angle threshHigh;
    private double multiplier;

    private boolean accelerating;

    /**
     * Creates a new {@code BangBangVelocityCalculator} instance.
     *
     * From WPILib documentation:<p>
     * Always ensure that your motor controllers are set to "coast" before
     * attempting to control them with a bang-bang controller.
     *
     * <p> Ensure that your motor and encoder are reporting in 
     * the same direction before using this controller.
     */
    public BangBangVelocityCalculator() {
        bang = new BangBangController();
        rateLimiter = new SlewRateLimiter(5.0); // Ramp from 0 to 100 in 1/5 of a second
        multiplier = 1.0;

        threshLow = Angle.zero();
        threshHigh = Angle.zero();
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
     * Set how quickly the motor should decelerate from 100% to 0% output.
     * @param rampSeconds Time it takes to ramp down from 100% to 0% output.
     */
    public void setSpeedRamp(double rampSeconds) {
        this.rampSeconds = rampSeconds;
        rebuildRateLimiter();
    }

    /**
     * Get how quickly the motor is allowed to decelerate between from 100% to 0%.
     * @return Ramping in units per second.
     */
    public double getSpeedRamp() {
        return rampSeconds;
    }

    private void rebuildRateLimiter() {
        try {
            rateLimiter = new SlewRateLimiter(rampSeconds);
        } catch (ArithmeticException e) {
            DriverStation.reportError("Failed to set rate limit, did you set it to something other than 0?", true);
            rateLimiter = new SlewRateLimiter(5.0);
        }
    }

    @Override
    public void reset() {
        // No resetting necessary
    }

    @Override
    public double calculate(Angle currentVelocity, Angle targetVelocity) {

        // Define targets using thresholds
        Angle targetLow = targetVelocity;
        Angle targetHigh = targetVelocity;
        
        // targetLow = targetLow.sub(threshLow);
        targetLow = Angle.cwDeg(targetVelocity.getCWDeg() - threshLow.getCWDeg());
        targetHigh = Angle.cwDeg(targetVelocity.getCWDeg() + threshHigh.getCWDeg());
        
        // if (targetLow.lessThan(Angle.zero())) targetLow = Angle.zero();
        
        // System.out.println(targetLow.getCWDeg());

        // Determine if the velocity should be increasing or decreasing
        if (currentVelocity.getCWDeg() < targetLow.getCWDeg()) accelerating = true; // Velocity is below both targets
        if (currentVelocity.getCWDeg() > targetHigh.getCWDeg()) accelerating = false; // Velocity is above both targets

        // If the velocity is between the targets, the bang bang controller will continue to target the same target as before.
        double target = 0;
        if (accelerating) {
            target = targetHigh.getCWDeg();
        } else {
            target = targetLow.getCWDeg();
        }

        double bangOut = bang.calculate(currentVelocity.getCWDeg(), target);
        if (bangOut == 0) { // Only apply ramp on deceleration
            bangOut = rateLimiter.calculate(bangOut);
        } else {
            rateLimiter.calculate(bangOut);
        }
        return bangOut * multiplier;
    }
}
