package com.swrobotics.lib.motor;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.math.Angle;


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

    @Override
    protected void percent(double percent) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void velocity(Angle velocity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void angle(Angle angle) {
        // TODO Auto-generated method stub
        
    }

}