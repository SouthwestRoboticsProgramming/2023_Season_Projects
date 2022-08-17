package com.team2129.lib.motor.calculators;

import com.team2129.lib.math.Angle;

public interface PositionCalculator {
    public double calculate(Angle current, Angle target);
}
