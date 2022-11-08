package com.swrobotics.lib.motor.calc;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.net.NTDouble;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;

/**
 * Calculates the percent output to set a motor's velocity using a
 * feedforward controller. 
 */
public final class FeedForwardVelocityCalculator implements VelocityCalculator {
    private SimpleMotorFeedforward ff;

    /**
     * Creates a new instance with the specified fixed gains,
     * with the acceleration gain set to a default of zero.
     * 
     * @param kS static gain
     * @param kV velocity gain
     */
    public FeedForwardVelocityCalculator(double kS, double kV) {
        this(kS, kV, 0);
    }

    /**
     * Creates a new instance with the specified fixed gains.
     * 
     * @param kS static gain
     * @param kV velocity gain
     * @param kA acceleration gain
     */
    public FeedForwardVelocityCalculator(double kS, double kV, double kA) {
        recreate(kS, kV, kA);
    }

    /**
     * Creates a new instance with the specified tunable gains, with 
     * the acceleration gain set to a default of zero. The values used 
     * will be automatically adjusted if the NetworkTables entries 
     * are changed.
     * 
     * @param kS static gain entry
     * @param kV velocity gain entry
     */
    public FeedForwardVelocityCalculator(NTDouble kS, NTDouble kV) {
        this(kS.get(), kV.get());
        kS.onChange(() -> recreate(kS.get(), kV.get(), 0));
        kV.onChange(() -> recreate(kS.get(), kV.get(), 0));
    }

    /**
     * Creates a new instance with the specified tunable gains.
     * The values used will be automatically adjusted if the NetworkTables
     * entries are changed.
     * 
     * @param kS static gain entry
     * @param kV velocity gain entry
     * @param kA acceleration gain entry
     */
    public FeedForwardVelocityCalculator(NTDouble kS, NTDouble kV, NTDouble kA) {
        this(kS.get(), kV.get(), kA.get());
        kS.onChange(() -> recreate(kS.get(), kV.get(), kA.get()));
        kV.onChange(() -> recreate(kS.get(), kV.get(), kA.get()));
        kA.onChange(() -> recreate(kS.get(), kV.get(), kA.get()));
    }

    // Recreates the feedforward when parameters change
    private void recreate(double kS, double kV, double kA) {
        ff = new SimpleMotorFeedforward(kS, kV, kA);
    }

    @Override
    public void reset() {
        // No reset necessary
    }

    @Override
    public double calculate(Angle currentVelocity, Angle targetVelocity) {
        return ff.calculate(currentVelocity.cw().deg(), targetVelocity.cw().deg());
    }
}
