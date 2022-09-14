package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTDouble;

/** 
 * A velocity calculator that estimates the output based
 * on maximum output and demanded output
 */
public class EstimateVelocityCalculator implements VelocityCalculator {

    private final NTDouble maximumVelocityRPS;

    public EstimateVelocityCalculator(NTDouble maximumVelocityRPS) {
        this.maximumVelocityRPS = maximumVelocityRPS;
    }

    @Override
    public void reset() {}

    /**
     * @param nullAngle This calculator is open loop, there is no need for this angle.
     */
    @Override
    public double calculate(Angle nullAngle, Angle targetVelocity) {
        try {
            return targetVelocity.getCWRot() / maximumVelocityRPS.get();
        } catch (ArithmeticException e) {
            return 0.0;
        }
    }

}
