package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.wpilib.AbstractRobot;

import edu.wpi.first.math.controller.PIDController;

/**
 * Calculates the percent output to set a motor's position using a
 * PID controller. 
 */
public final class PIDPositionCalculator implements PositionCalculator {
    private final PIDController pid;

    /**
     * Creates a new instance with the specified fixed PID constants.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     */
    public PIDPositionCalculator(double kP, double kI, double kD) {
        pid = new PIDController(kP, kI, kD, 1 / AbstractRobot.get().getPeriodicPerSecond());
    }

    /**
     * Creates a new instance with the specified tunable PID constants.
     * The values used will be automatically adjusted if the NetworkTables
     * entry is changed.
     * 
     * @param kP proportional coefficient entry
     * @param kI integral coefficient entry
     * @param kD derivative coefficient entry
     */
    public PIDPositionCalculator(NTDouble kP, NTDouble kI, NTDouble kD) {
        this(kP.get(), kI.get(), kD.get());
        kP.onChange(() -> pid.setP(kP.get()));
        kI.onChange(() -> pid.setI(kI.get()));
        kD.onChange(() -> pid.setD(kD.get()));
    }

    /**
     * Enables continuous input by treating the min and max values
     * as the same value, which allows it to find the shortest route
     * to the target value.
     * 
     * @param min minimum value
     * @param max maximum value
     * @see PIDController#enableContinuousInput(double, double)
     */
    public void enableContinuousInput(double min, double max) {
        pid.enableContinuousInput(min, max);
    }

    @Override
    public void reset() {
        pid.reset();
    }

    @Override
    public double calculate(Angle currentPos, Angle targetPos) {
        return pid.calculate(currentPos.getCWDeg(), targetPos.getCWDeg());
    }
}
