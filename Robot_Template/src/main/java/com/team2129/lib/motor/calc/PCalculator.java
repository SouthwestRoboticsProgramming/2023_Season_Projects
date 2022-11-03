package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTDouble;

public final class PCalculator implements PositionCalculator, VelocityCalculator {
    private double kP;

    public PCalculator(double kP) {
        this.kP = kP;
    }

    public PCalculator(NTDouble kP) {
        this.kP = kP.get();
        kP.onChange(() -> setKP(kP.get()));
    }

    public void setKP(double kP) {
        this.kP = kP;
    }

    @Override
    public double calculate(Angle current, Angle target) {
        double currentAng = current.getCWDeg();
        double targetAng = target.getCWDeg();
        double error = targetAng - currentAng;

        return error * kP;
    }

    @Override
    public void reset() {
    }
}