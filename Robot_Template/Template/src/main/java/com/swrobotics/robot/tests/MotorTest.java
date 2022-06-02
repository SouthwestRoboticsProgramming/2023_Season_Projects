package com.swrobotics.robot.tests;

import java.sql.Time;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.encoder.CANCoderImplementation;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.MotorMode;
import com.swrobotics.lib.motor.TalonMotor;

public class MotorTest {
    // Class for testing the motor rework in swrobotics.lib

    /*
    Features that motors should have:
    - Spin motor at voltage
    - Turn to angle
    - Spin at velocity (flywheel)
    - Stop
    */

    private final TalonMotor fx;
    private final TalonMotor srx;
    private final CANCoderImplementation encoder;


    // Two motors: A falcon, and a Johnson electric with CTRE Encoder breakout board
    public MotorTest() {
        // Create unconfigured motors (Control for optimizing CAN)
        TalonFX fx_toWrap = new TalonFX(10);
        TalonSRX srx_toWrap = new TalonSRX(11);

        // Configure motors
        fx_toWrap.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        srx_toWrap.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);


        // Wrap the motors
        fx = new TalonMotor(fx_toWrap);
        srx = new TalonMotor(srx_toWrap);

        // TODO: Build a setup with a CANCoder or something.
        encoder = new CANCoderImplementation(12);
    }

    public void testStop(MotorMode stopMode, double percentOut) {
        // Spin it up
        fx.set(MotorMode.PERCENT_OUT, percentOut);
        srx.set(MotorMode.PERCENT_OUT, percentOut);

        // Test getting velocity
        if (fx.getVelocity().greaterThan(Angle.cwDeg(180))) {
            fx.set(stopMode);
        }

        if (srx.getVelocity().greaterThan(Angle.cwDeg(180))) {
         srx.set(stopMode);
        }
    }
}
