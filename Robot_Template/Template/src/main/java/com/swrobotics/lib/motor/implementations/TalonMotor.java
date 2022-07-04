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
    private TalonInternalEncoder internalEncoder;
    private double ticksPerRotation;


    /**
     * Create a TalonMotor without an encoder to wrap around an existing CTRE motor controller.<br>
     * <pre>{@code
     * // Use this formatting to add an encoder for closed-loop
     * talon = new TalonMotor(baseTalon);
     * talon.assignEncoder(encoder);
     * }
     * @param talon CTRE motor controller to wrap. Note: This does include the Victor SPX.
     */
    public TalonMotor(BaseTalon talon) {
        this.talon = talon;
    }

    /**
     * Get the encoder that is either plugged into the motor controller, or the enternal encoder of a Falon 500.
     * @param ticksPerRotation The number of encoder ticks per real rotation of the motor.
     * @return The internal encoder of the motor, seperated into an Encoder object.
     */
    public Encoder getInternalEncoder(double ticksPerRotation) {
        if (internalEncoder == null || this.ticksPerRotation != ticksPerRotation) {
            internalEncoder = new TalonInternalEncoder(talon, ticksPerRotation);
        }
        this.ticksPerRotation = ticksPerRotation;
        return internalEncoder;
    }

    @Override
    protected void setPercent(double percent) {
        talon.set(ControlMode.PercentOutput, percent);
        
    }
}