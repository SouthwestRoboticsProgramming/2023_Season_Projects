package com.swrobotics.lib.motor;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.encoder.Encoder;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;


/**
 * A class to build a TalonMotor with the settings already configured.
 */
public class TalonMotorBuilder {

    // Note to self: Create an internal encoder in this then pass that (If no other encoder is specifie) into the TalonMotor

    private BaseTalon talon;
    private Encoder external;
    
    private ProfiledPIDController pid;
    private SimpleMotorFeedforward feed;

    public TalonMotorBuilder(BaseTalon talon) {
        this.talon = talon;
    }

    public TalonMotorBuilder setPID(double kP, double kI, double kD, TrapezoidProfile.Constraints constraints) {
        pid = new ProfiledPIDController(kP, kI, kD, constraints);
        return this;
    }

    public TalonMotorBuilder setPID(double kP, double kI, double kD) {
        pid = new ProfiledPIDController(kP, kI, kD, new TrapezoidProfile.Constraints(Double.MAX_VALUE, Double.MAX_VALUE));
        return this;
    }

    public TalonMotorBuilder setPID(ProfiledPIDController pid) {
        this.pid = pid;
        return this;
    }

    public TalonMotorBuilder setFeedforward(SimpleMotorFeedforward feed) {
        this.feed = feed;
        return this;
    }

    public TalonMotorBuilder setFeedforward(double kS, double kV) {
        feed = new SimpleMotorFeedforward(kS, kV);
        return this;
    }

    public TalonMotorBuilder setFeedforward(double kS, double kV, double kA) {
        feed = new SimpleMotorFeedforward(kS, kV, kA);
        return this;
    }

    public TalonMotorBuilder setExternalEncoder(Encoder encoder) {
        external = encoder;
        return this;
    }



    public TalonMotor build() {
        return new TalonMotor(talon); // TODO
    }

}
