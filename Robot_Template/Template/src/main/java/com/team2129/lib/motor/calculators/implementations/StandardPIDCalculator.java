package com.team2129.lib.motor.calculators.implementations;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calculators.PositionCalculator;
import com.team2129.lib.motor.calculators.VelocityCalculator;
import com.team2129.lib.net.NTDouble;

import edu.wpi.first.math.controller.PIDController;

public class StandardPIDCalculator implements PositionCalculator, VelocityCalculator {
    private final PIDController pid;

    /**
     * Create a PIDCalculator using a pre-configured PIDController.
     * @param pidController Pre-configured PIDController.
     */
    public StandardPIDCalculator(PIDController pidController) {
        pid = pidController;
    }

    // TODO: Other constructors

    @Override
    public double calculate(Angle current, Angle target) {
        return 0;
    }
    
}
