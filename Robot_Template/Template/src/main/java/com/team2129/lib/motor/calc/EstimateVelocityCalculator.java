package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;

/** 
 * A velocity calculator that estimates the output based
 * on maximum output and demanded output
 */
public class EstimateVelocityCalculator implements VelocityCalculator {

    private final Angle maximumVelocity;

    public EstimateVelocityCalculator(Angle maximumVelocity) {
        this.maximumVelocity = maximumVelocity;
    }

    @Override
    public void reset() {}

    /**
     * @param nullAngle This calculator is open loop, there is no need for this angle.
     */
    @Override
    public double calculate(Angle nullAngle, Angle targetVelocity) {
        return targetVelocity.getCWDeg() / maximumVelocity.getCWDeg();
    }

}
