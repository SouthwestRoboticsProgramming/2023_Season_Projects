package com.swrobotics.lib.motor;

import com.swrobotics.lib.math.Angle;

public interface Motor {
    /**
     * Updates the PID controller and the motor's velocity. This method
     * should be called once per periodic when the motor is active. It
     * can be called when it is not active, but it is not neccessary.
     */
    public void update();

    /**
     * Gets the motor's rotational position relative to its starting
     * position.
     * 
     * @return Rotational position in clockwise radians
     */
    public Angle getPosition();

    /**
     * Resets the motor's rotational position to zero. This sets the
     * current position to correspond to a position of zero.
     */
    public void zeroPosition();

    /**
     * Sets the motor's position to a given value. This will cause the
     * current position to correspond to the given value.
     * 
     * @param position Rotational position in clockwise radians
     */
    public void setPosition(Angle position);

    /**
     * Gets the motor's rotational velocity in revolutions per minute.
     * A positive velocity corresponds to clockwise rotation.
     * 
     * @return Velocity in revolutions per minute
     */
    public double getVelocity();

    /**
     * Runs this motor with the specified output power. If the given
     * power falls outside the range of {@link #setOutputClamp}, it
     * will be clamped to stay inside the range. A positive power value
     * corresponds to clockwise rotation.
     * 
     * Note: The percent output power affects both the speed and the
     *       torque of the motor. This means that stalling is more
     *       likely with a lower percent power.
     * 
     * @param power Percentage of power from -1 to 1
     */
    public void runAtPercentPower(double power);

    /**
     * Attempts to turn the motor to a target position. The position is
     * defined as returned by {@link getPosition}.
     * 
     * @param position Target position
     */
    public void targetPosition(double position);

    /**
     * Runs this motor at the specified RPM. Positive RPM values
     * correspond to clockwise rotation, and negative corresponds
     * to counterclockwise.
     * 
     * @param rpm Speed in RPM
     */
    public void runAtRPM(double rpm);

    /**
     * Stops this motor by providing no output power, allowing it
     * to coast.
     */
    public void stop();

    /**
     * Stops this motor by maintaining an RPM of zero.
     */
    public void halt();
    
    /**
     * Stops this motor by constantly targeting its current position.
     */
    public void hold();

    /**
     * Sets the proportional, integral, and derivative coefficients of
     * the motor's PID controller.
     * 
     * @param kP Proportional coefficient
     * @param kI Integral coefficient
     * @param kD Derivative coefficient
     */
    public void setPID(double kP, double kI, double kD);

    /**
     * Resets the PID controller's integral accumulator back to zero.
     */
    public void resetPID();

    /**
     * Sets the minimum and maximum output percentages of this motor.
     * The min and max values are in percent output, from 0 to 1.
     * The default range is [-1, 1].
     * 
     * @param min Minimum output percentage
     * @param max Maximum output percentage
     */
    public void setOutputClamp(double min, double max);
}
