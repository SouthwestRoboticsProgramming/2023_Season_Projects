package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTDouble;

// TODO: Is there a better name for this?

/**
 * Calculates the percent output to set a motor's velocity using
 * both a PID controller and a feedforward controller. 
 */
public final class PIDFFVelocityCalculator implements VelocityCalculator {
    private final PIDVelocityCalculator pid;
    private final FeedForwardVelocityCalculator ff;

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
        pid = new PIDVelocityCalculator(kP, kI, kD);
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
        pid = new PIDVelocityCalculator(kP, kI, kD);
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
        pid = new PIDVelocityCalculator(kP, kI, kD);
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
        pid = new PIDVelocityCalculator(kP, kI, kD);
        ff = new FeedForwardVelocityCalculator(kS, kV, kA);
    }

    @Override
    public void reset() {
        pid.reset();
    }

    @Override
    public double calculate(Angle currentVelocity, Angle targetVelocity) {
        return pid.calculate(currentVelocity, targetVelocity) + ff.calculate(currentVelocity, targetVelocity);
    }
}
