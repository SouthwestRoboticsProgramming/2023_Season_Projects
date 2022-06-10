package com.swrobotics.lib.motor.implementations;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.encoder.TalonInternalEncoder;

import com.swrobotics.lib.motor.Motor;


public class TalonMotor extends Motor {

    private final BaseTalon talon;


    /**
     * Create a TalonMotor to wrap around an existing CTRE Motor controller.
     * @param talon CTRE Motor controller to wrap. Note: This does include the Victor SPX.
     */
    public TalonMotor(BaseTalon talon) {
        this.talon = talon;
 
    }

    public TalonMotor(BaseTalon talon, Encoder encoder) {
        super(encoder);
        this.talon = talon;

    }

    public Encoder getInternalEncoder(double ticksPerRotation) {
        return new TalonInternalEncoder(talon, ticksPerRotation);
    }

    @Override
    protected void percent(double percent) {
        talon.set(ControlMode.PercentOutput, percent);
        
    }

    // TODO: To string
    @Override
    public String toString() {
        return "TODO";
    }
    

}