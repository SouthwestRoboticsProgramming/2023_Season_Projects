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
        rateLimiter = new SlewRateLimiter(5.0); // Ramp from 0 to 100 in 1/5 of a second
        threshold = Angle.zero();
        multiplier = 1.0;
    }

    /**
     * Adjust when the motor should shut off when approaching the setpoint.
     * This can be tuned to create a perfect ramp as deceleration is not instant and with no threshold, the controller will tend to overshoot the setpoint.
     * @param threshold Difference between the current velocity and the target velocity where the motor should shut off.
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
     * @return Time it takes to ramp from 100% to 0% motor output.
     */
    public double getSpeedRamp() {
        return rampSeconds;
    }

    private void rebuildRateLimiter() {
        try {
            rateLimiter = new SlewRateLimiter(1 / rampSeconds);
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
        // Pretend that the target is lower to account for the threshold.
        double bangOut = bang.calculate(currentVelocity.getCWDeg(), targetVelocity.sub(threshold).getCWDeg());
        if (bangOut == 0) { // Only apply ramp on deceleration
            bangOut = rateLimiter.calculate(bangOut);
        }
        return bangOut * multiplier;
    }
}
