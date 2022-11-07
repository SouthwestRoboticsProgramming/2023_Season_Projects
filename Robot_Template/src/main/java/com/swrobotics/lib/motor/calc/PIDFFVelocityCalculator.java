package com.swrobotics.lib.motor.calc;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.net.NTDouble;

// TODO: Is there a better name for this?

/**
 * Calculates the percent output to set a motor's velocity using
 * both a PID controller and a feedforward controller. 
 */
public final class PIDFFVelocityCalculator implements VelocityCalculator {
    private final PIDCalculator pid;
    private final FeedForwardVelocityCalculator ff;

    private double pidMultiplier;
    private double ffMultiplier;

    /**
     * Creates a new instance with the specified fixed PID and feedforward constants,
     * with a default acceleration gain of zero.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     * @param kS static gain
     * @param kV velocity gain
     */
    public PIDFFVelocityCalculator(double kP, double kI, double kD, double kS, double kV) {
        pid = new PIDCalculator(kP, kI, kD);
        ff = new FeedForwardVelocityCalculator(kS, kV);
    }

    /**
     * Creates a new instance with the specified fixed PID and feedforward constants.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     * @param kS static gain
     * @param kV velocity gain
     * @param kA acceleration gain
     */
    public PIDFFVelocityCalculator(double kP, double kI, double kD, double kS, double kV, double kA) {
        pid = new PIDCalculator(kP, kI, kD);
        ff = new FeedForwardVelocityCalculator(kS, kV, kA);
    }

    /**
     * Creates a new instance with the specified fixed PID and feedforward constants,
     * with a default acceleration gain of zero.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     * @param kS static gain
     * @param kV velocity gain
     */
    public PIDFFVelocityCalculator(NTDouble kP, NTDouble kI, NTDouble kD, NTDouble kS, NTDouble kV) {
        pid = new PIDCalculator(kP, kI, kD);
        ff = new FeedForwardVelocityCalculator(kS, kV);
    }

    /**
     * Creates a new instance with the specified fixed PID and feedforward constants.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     * @param kS static gain
     * @param kV velocity gain
     * @param kA acceleration gain
     */
    public PIDFFVelocityCalculator(NTDouble kP, NTDouble kI, NTDouble kD, NTDouble kS, NTDouble kV, NTDouble kA) {
        pid = new PIDCalculator(kP, kI, kD);
        ff = new FeedForwardVelocityCalculator(kS, kV, kA);
    }

    /**
     * Control how much of the motor output is determined by PID and how much is determined by Feedforward control.
     * NOTE: These two multipliers can, and probably should have a sum greater than or equal to 1.
     * 
     * @param pidMultiplier Multiplier applied to the PID output
     * @param feedforwardMultiplier Multiplier applied to the feedforward output.
     */
    public void setOutputMix(double pidMultiplier, double feedforwardMultiplier) {
        this.pidMultiplier = pidMultiplier;
        ffMultiplier = feedforwardMultiplier;
    }

    /**
     * Get the PID multiplier set by {@code setOutputMix()}.
     * @return PID multiplier
     */
    public double getPIDMultiplier() {
        return pidMultiplier;
    }

    /**
     * Get the Feedforward multiplier set by {@code setOutputMix()}.
     * @return Feedforward multiplier
     */
    public double getFeedforwardMultiplier() {
        return pidMultiplier;
    }

    @Override
    public void reset() {
        pid.reset();
    }

    @Override
    public double calculate(Angle currentVelocity, Angle targetVelocity) {
        return pid.calculate(currentVelocity, targetVelocity) * pidMultiplier
        + ff.calculate(currentVelocity, targetVelocity) * ffMultiplier;
    }
}
