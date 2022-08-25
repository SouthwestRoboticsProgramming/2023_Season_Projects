package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.wpilib.AbstractRobot;

import edu.wpi.first.math.controller.PIDController;

/**
 * Calculates the percent output to set a motor's velocity using a
 * PID controller. 
 */
public final class PIDVelocityCalculator implements VelocityCalculator {
    private final PIDController pid;

    /**
     * Creates a new instance with the specified fixed PID constants.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     */
    public PIDVelocityCalculator(double kP, double kI, double kD) {
        pid = new PIDController(kP, kI, kD, 1 / AbstractRobot.get().getPeriodicPerSecond());
    }

    /**
     * Creates a new instance with the specified tunable PID constants.
     * The values used will be automatically adjusted if the NetworkTables
     * entries are changed.
     * 
     * @param kP proportional coefficient entry
     * @param kI integral coefficient entry
     * @param kD derivative coefficient entry
     */
    public PIDVelocityCalculator(NTDouble kP, NTDouble kI, NTDouble kD) {
        this(kP.get(), kI.get(), kD.get());
        kP.onChange(() -> pid.setP(kP.get()));
        kI.onChange(() -> pid.setI(kI.get()));
        kD.onChange(() -> pid.setD(kD.get()));
    }

    @Override
    public void reset() {
        pid.reset();
    }

    @Override
    public double calculate(Angle currentVel, Angle targetVel) {
        return pid.calculate(currentVel.getCWDeg(), targetVel.getCWDeg());
    }
}
