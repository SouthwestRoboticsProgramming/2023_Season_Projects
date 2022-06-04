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
    private SimpleMotorFeedforward feed;
    private final BangBangController bang;

    private boolean flywheel;

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


    /**
     * Rebuild the feedforward controller to update its values.
     * @param kS Static gain
     * @param kV Velocity gain
     * @param kA Acceleration gain (OPTIONAL: Leave 0 if undefined)
     */
    public void rebuildFeedforward(double kS, double kV, double kA) {
        if (kA == 0) {
            feed = new SimpleMotorFeedforward(kS, kV);
            return;
        }

        feed = new SimpleMotorFeedforward(kS, kV, kA);
    }

    public void setPID(double kP, double kI, double kV) {
        pid.setPID(kP,kI,kV);
    }

    public void setFlywheelMode(boolean isFlywheel) {
        flywheel = isFlywheel;
    }

    @Override
    protected void percent(double percent) {
        talon.set(ControlMode.PercentOutput, percent);
        
    }

    @Override
    protected void velocity(Angle current, Angle target) {
        double feedOut = feed.calculate(target.getCWDeg());

        if (flywheel) {
            double bangOut = bang.calculate(current.getCWDeg(), target.getCWDeg());
            talon.set(ControlMode.PercentOutput, bangOut + 0.9 * feedOut);
            return;
        }


        double pidOut = pid.calculate(current.getCWDeg(), target.getCWDeg());
        
        talon.set(ControlMode.PercentOutput, pidOut + feedOut);
        
    }

    @Override
    protected void angle(Angle current, Angle target) {
        double output = pid.calculate(current.getCWDeg(), target.getCWDeg());
        talon.set(ControlMode.PercentOutput, output);
    }

}