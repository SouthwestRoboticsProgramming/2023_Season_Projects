package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;

public class CompoundVelocityCalculator implements VelocityCalculator {

    private final VelocityCalculator[] calculators;

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
