package com.team2129.lib.motor;

import com.team2129.lib.math.Angle;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

import java.util.function.Supplier;

import com.team2129.lib.encoder.Encoder;

import com.team2129.lib.wpilib.AbstractRobot;
import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * An abstract class to put all motor functions into one interface for better motor controls and uniform code across all vendors.
 */
public abstract class Motor implements Subsystem {
    private PIDController pid;
    private SimpleMotorFeedforward feed;
    private final BangBangController bang;

    private Encoder encoder;
    private Angle holdTarget;
    private boolean isHolding;

    private boolean isFlywheel;

    private Runnable controlMode;
    private double kF;

    /**
     * Create a motor wrapping a vendor-specific motor. <br>
     * 
     * <pre>{@code
     * // Use this formatting for encoders
     * motor = new Motor();
     * motor.assignEncoder();
     * }
     */
    public Motor(Subsystem parent) {
        controlMode = () -> {};

        pid = new PIDController(0.0, 0.0, 0.0, 1 / AbstractRobot.get().getPeriodicPerSecond());
        feed = new SimpleMotorFeedforward(0.0, 0.0);
        bang = new BangBangController();

        isFlywheel = false;
        kF = 0;

        // Schedule this and attach to parent subsystem
        Scheduler.get().addSubsystem(parent, this);
    }

    /**
     * Set the profiled PID controller for position control with the motor.
     * @param pid A configured PID controller.
     */
    public void setPIDController(PIDController pid) {
        this.pid = pid;
    }

    /**
     * Set the feedforward controller for velocity control with the motor.
     * @param feed A configured feedforward controller.
     */
    public void setFeedforward(SimpleMotorFeedforward feed) {
        this.feed = feed;
    }

    /**
     * Sets the feedforward coefficient for velocity control.
     * @param kF Feedforward coefficient
     */
    public void setKF(double kF) {
        this.kF = kF;
    }

    /**
     * Gives the motor an absolute encoder
     * @param encoder Encoder implementation
     */
    public void assignEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Get the encoder being used by the motor for position and velocity control.
     * @return The assigned encoder for the motor.
     */
    public Encoder getEncoder() {
        return encoder;
    }

    /**
     * Change the way that velocity control is configured by using the motor as a flywheel controller. WARNING: Congigure the motor to turn off break mode!
     * @param isFlywheel Set if the motor is controlling a flywheel.
     */
    public void setFlywheelMode(boolean isFlywheel) {
        this.isFlywheel = isFlywheel;
    }


    /**
     * Give the motor a percentage of the voltage recieved.
     * @param percent The demanded percent out of the motor.
     */
    public void percent(double percent) {
        controlMode = () -> setPercent(percent);
        isHolding = false;
    }

    /**
     * Control the motor to spin at a specified velocity.
     * @param target The target velocity of the motor.
     */
    public void velocity(Angle target) {
        if (encoder == null) {
            DriverStation.reportError("No assigned encoder, cannot control velocty", true);
            return;
        }

        controlMode = () -> setVelocity(target);
        isHolding = false;
    }

    /**
     * Control the motor to target an angular position.
     * @param target The target angle of the motor.
     */
    public void angle(Angle target) {
        angle(encoder::getAngle, target);
    }

    /**
     * Control the motor to target an angular position.
     * @param current Supplier for the current position of the motor.
     * @param target The target angle of the motor.
     */
    public void angle(Supplier<Angle> current, Angle target) {
        if (encoder == null) {
            DriverStation.reportError("No assigned encoder, cannot control position", true);
            return;
        }

        controlMode = () -> setAngle(current, target);
        isHolding = false;
    }

    /**
     * Set the motor to 0% power. The motor will slowly wind down.
     */
    public void stop() {
        percent(0);
    }

    /**
     * Set the motor to target a velocity of zero.
     */
    public void halt() {
        velocity(Angle.cwDeg(0));
    }

    /**
     * Set the motor to hold the current position. If it is moved, it will target the initial position.
     */
    public void hold() {
        if (encoder == null) {
            DriverStation.reportError("No assigned encoder, cannot hold position", true);
            return;
        }

        if (!isHolding) {
            holdTarget = encoder.getAngle();
        }
        isHolding = true;

        controlMode = () -> setAngle(encoder::getAngle, holdTarget);
    }

    /**
     * Actually set the motor output. This is the implementation for motor usage.
     * 
     * @param percent Demanded percent -1 - 1 output for the motor.
     */
    protected abstract void setPercent(double percent);

    private void setVelocity(Angle target) {
        double out;

        Angle currentVelocity = encoder.getVelocity();
        if (isFlywheel) {
            out = bang.calculate(currentVelocity.getCWDeg(), target.getCWDeg())  +  feed.calculate(target.getCWDeg() * 0.9); 
        } else {
            double pidOut = pid.calculate(currentVelocity.getCWDeg(), target.getCWDeg());
            double feedOut = feed.calculate(target.getCWDeg() * 0.9);
            out = pidOut  + feedOut ;
        }

        setPercent(out);
    }
    
    private void setAngle(Supplier<Angle> current, Angle target) {
        double out = pid.calculate(current.get().getCWDeg(), target.getCWDeg());
        setPercent(out);
    }

    @Override
    public void periodic() {
        controlMode.run();
    }
}
