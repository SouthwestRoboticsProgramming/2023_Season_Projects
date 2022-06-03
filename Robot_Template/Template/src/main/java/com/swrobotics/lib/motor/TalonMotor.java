package com.swrobotics.lib.motor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.math.Angle;

import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;


public class TalonMotor extends Motor {

    private final BaseTalon talon;

    private final ProfiledPIDController pid;
    private final SimpleMotorFeedforward feed;
    private final BangBangController bang;

    /**
     * Create a TalonMotor to wrap around an existing CTRE Motor controller.
     * @param talon CTRE Motor controller to wrap. Note: This does include the Victor SPX.
     */
    public TalonMotor(BaseTalon talon, ProfiledPIDController pid, SimpleMotorFeedforward feed) {
        this.talon = talon;
        this.pid = pid;
        this.feed = feed;
        bang = new BangBangController();
 
    }

    public TalonMotor(BaseTalon talon, Encoder encoder, ProfiledPIDController pid, SimpleMotorFeedforward feed) {
        super(encoder);
        this.talon = talon;
        this.pid = pid;
        this.feed = feed;
        bang = new BangBangController();
    }

    @Override
    protected void percent(double percent) {
        talon.set(ControlMode.PercentOutput, percent);
        
    }

    @Override
    protected void velocity(Angle current, Angle target) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void angle(Angle current, Angle target) {
        // TODO Auto-generated method stub
        
    }

}