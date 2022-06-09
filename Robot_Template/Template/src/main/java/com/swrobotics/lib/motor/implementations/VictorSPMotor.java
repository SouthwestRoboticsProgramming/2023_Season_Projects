package com.swrobotics.lib.motor.implementations;

import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.Motor;

import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class VictorSPMotor extends Motor {

    private final VictorSP victor;

    // private final ProfiledPIDController pid;
    // private SimpleMotorFeedforward feed;
    // private final BangBangController bang;

    private boolean flywheel;

    public VictorSPMotor(VictorSP victor) {
        this.victor = victor;
    }

    // public VictorSPMotor(VictorSP victor, Encoder externalEncoder) {
    //     super(externalEncoder);
    //     this.victor = victor;
    // }

    // public VictorSPMotor(VictorSP victor, Encoder externalEncoder, )

    @Override
    public Encoder getInternalEncoder(double ticksPerRotation) {
        throw new RuntimeException("Victor SP does not have encoder support");
    }

    @Override
    protected void percent(double percent) {
        victor.set(percent);
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
