package com.swrobotics.lib.motor.calc;

import com.swrobotics.lib.math.Angle;

/**
 * Class to combine multiple velocity calculators into one.
 * The outputs of each of the calculators are added together to form the output.
 */
public final class CompoundVelocityCalculator implements VelocityCalculator {

    private final VelocityCalculator[] calculators;

    /**
     * Create a CompoundVelocityCalculator using multiple other VelocityCalculators.
     * @param calculators As many VelocityCalculators as you want.
     */
    public CompoundVelocityCalculator(VelocityCalculator... calculators) {
        this.calculators = calculators;
    }

    @Override
    public void reset() {
        for (VelocityCalculator velocityCalculator : calculators) {
            velocityCalculator.reset();
        }
    }

    @Override
    public double calculate(Angle currentVelocity, Angle targetVelocity) {
        double total = 0;

        for (VelocityCalculator velocityCalculator : calculators) {
            total += velocityCalculator.calculate(currentVelocity, targetVelocity);
        }

        return total;
    }
}
