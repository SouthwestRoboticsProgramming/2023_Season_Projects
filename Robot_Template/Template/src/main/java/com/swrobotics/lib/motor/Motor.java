package com.swrobotics.lib.motor;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.encoder.AbsoluteEncoder;

public interface Motor {

    /**
     * 
     * @param mode The mode that you want the motor to run with
     * @param demand Applicibale unit to mode (i.e: Velocity - RPM, Voltage - Volts)
     */
    public void set(MotorMode mode, double demand);
    default void set(MotorMode mode) { set(mode, 0); }

    /**
     * Gets the motors position relative to the starting position or the zero'd position
     * @return Rotational position in clockwise radians
     */
    public Angle getPosition();

    /**
     * Get the velocity of the motor in RPM
     * @return The RPM of the motor
     */
    public double getVelocity();


    /**
     * Gives the motor an absolute encoder
     * @param encoder Encoder implementation
     */
    public void setAbsoluteSensor(AbsoluteEncoder encoder);



    /**
     * 
     * @param position Angular position of the motor in clockwise radians
     */
    public void setPosition(Angle position);
    
    /**
     * Set the current position of the motor to zero
     */
    public void zeroPosition();


    /**
     * Sets the coefficients of the motor's PID controller.
     * @param kP Proportional -> Based on the error
     * @param kI Integral -> Based on time that the error has existed
     * @param kD Derivative -> Based on the acceleration differnce
     */
    public void setPID(double kP, double kI, double kD);



}
