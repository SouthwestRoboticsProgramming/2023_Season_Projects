package com.swrobotics.lib.motor.implementations;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.encoder.TalonInternalEncoder;

import com.swrobotics.lib.motor.Motor;

/**
 * A wrapper class for CTRE Talon motor controllers.
 */
public class TalonMotor extends Motor {

    private final BaseTalon talon;


    /**
     * Create a TalonMotor to wrap around an existing CTRE motor controller.
     * @param talon CTRE motor controller to wrap. Note: This does include the Victor SPX.
     */
    public TalonMotor(BaseTalon talon) {
        this.talon = talon;
 
    }

    /**
     * Create a TalonMotor to wrap around and existing CtRE motor controller with an assigned encoder.
     * @param talon CTRE motor controller to wrap. Note: This does not include the Victor SPX.
     * @param encoder Encoder to assigne to the motor controller.
     */
    public TalonMotor(BaseTalon talon, Encoder encoder) {
        super(encoder);
        this.talon = talon;

    }

    /**
     * Get the encoder that is either plugged into the motor controller, or the enternal encoder of a Falon 500.
     * @param ticksPerRotation The numver of encoder ticks per real rotation of the motor.
     * @return
     */
    public Encoder getInternalEncoder(double ticksPerRotation) {
        return new TalonInternalEncoder(talon, ticksPerRotation);
    }

    @Override
    protected void setPercent(double percent) {
        talon.set(ControlMode.PercentOutput, percent);
        
    }

    

}