package com.team2129.lib.motor.calc;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.wpilib.AbstractRobot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;

/**
 * Calculates the percent output to set a motor's position using a
 * PID controller. 
 */
public final class PIDCalculator implements PositionCalculator, VelocityCalculator {
    private final PIDController pid;

    private boolean allowNegativeOutputs;

    /**
     * Creates a new instance with the specified fixed PID constants.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     */
    public PIDCalculator(double kP, double kI, double kD) {
        pid = new PIDController(kP, kI, kD, 1 / AbstractRobot.get().getPeriodicPerSecond());
        allowNegativeOutputs = true;
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
    public PIDCalculator(NTDouble kP, NTDouble kI, NTDouble kD) {
        this(kP.get(), kI.get(), kD.get());
        kP.onChange(() -> pid.setP(kP.get()));
        kI.onChange(() -> pid.setI(kI.get()));
        kD.onChange(() -> pid.setD(kD.get()));
    }

    /**
     * Set the proportional gain
     * @param kP Proportional coefficient
     */
    public void setP(double kP) {
        pid.setP(kP);
    }

    /**
     * Set the integral gain
     * @param kI Integral coefficient
     */
    public void setI(double kI) {
        pid.setI(kI);
    }


    /**
     * Set the derivative gain
     * @param kD Derivative coefficient
     */
    public void setD(double kD) {
        pid.setD(kD);
    }

    /**
     * From WPILib documentation: <br>
     * Sets the minimum and maximum values for the integrator.
     * 
     * When the cap is reached, the integrator value is added to the controller output 
     * rather than the integrator value times the integral gain.
     * 
     * @param minIntegral The minimum value of the integrator.
     * @param maxIntegral The maximum value of the integrator.
     */
    public void setIntegratorRange(double minIntegral, double maxIntegral) {
        pid.setIntegratorRange(minIntegral, maxIntegral);
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

    /**
     * Convert to a PIDController by getting the PIDController core to the PIDCalculator.
     * @return A PIDController with the same settings as the PIDCalculator.
     */
    public PIDController toPIDController() {
        return pid;
    }

    /**
     * Set the tolerance of the PIDCalculator where if the distance between the setpoint and current is withing this tolerance,
     * the PIDCalculator will stop attempting to get to the position.
     */
    public void setTolerance(Angle tolerance) {
        pid.setTolerance(tolerance.getCWDeg());
    }

    /**
     * Set if the PIDCalculator can output negative values.
     * This may be useful in situations where changing directions
     * could put stress on the motor.
     * @param allowNegativeOutputs
     */
    public void allowNegativeOutputs(boolean allowNegativeOutputs) {
        this.allowNegativeOutputs = allowNegativeOutputs;
    }

    @Override
    public void reset() {
        pid.reset();
    }

    @Override
    public double calculate(Angle current, Angle target) {

        // TODO: At setpoint that works
        double output = pid.calculate(current.getCWDeg(), target.getCWDeg());

        if (!allowNegativeOutputs) {
            output = MathUtil.clamp(output, 0, Double.MAX_VALUE);
        }

        // System.out.println(pid.getP() + " " + pid.getI() + " " + pid.getD());

        // System.out.println("curr: " + current.getCWDeg() + " tar: " + target.getCWDeg() + " out: " + output);

        return output;
    }
}
