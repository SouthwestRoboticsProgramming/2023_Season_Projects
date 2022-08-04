package com.team2129.lib.motor;

import com.team2129.lib.math.Angle;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;
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

    private Angle holdAngle; // Recorded to save the angle for the HOLD mode
    private boolean isHolding; // Record to set the holdAngle to the current.
    private boolean isFlywheel;

    private Angle currentAngle;
    private Angle currentVelocity;

    private Runnable controlMode;


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
     * Gives the motor an absolute encoder
     * @param encoder Encoder implementation
     */
    public void assignEncoder(Encoder encoder) {
        this.encoder = encoder;
        currentAngle = encoder.getAngle();
        currentVelocity = encoder.getVelocity();
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
        controlMode = () -> {
            percent(percent);
        };

        setPercent(percent);
    }

    /**
     * Actually set the motor output. This is the implementation for motor usage.
     * 
     * @param percent Demanded percent -1 - 1 output for the motor.
     */
    protected abstract void setPercent(double percent);

    /**
     * Control the motor to spin at a specified speed.
     * @param target The target velocity of the motor.
     */
    public void velocity(Angle target) {
        controlMode = () -> {
            velocity(target);
        };

        if (encoder == null) {
            DriverStation.reportError("No assigned encoder, cannot control velocty", true);
            return;
        }

        double out;

        if (isFlywheel) {
            out = bang.calculate(currentVelocity.getCWDeg(), target.getCWDeg())  +  feed.calculate(target.getCWDeg() * 0.9); 
        } else {
            double pidOut = pid.calculate(currentVelocity.getCWDeg(), target.getCWDeg());
            double feedOut = feed.calculate(target.getCWDeg() * 0.9);
            out = pidOut  + feedOut ;
        }

        setPercent(out);
    }

    /**
     * Control the motor to target an angular position.
     * @param target The target angle of the motor.
     */
    public void angle(Angle target) {
        angle(currentAngle, target);
    }
    
    /**
     * Control the motor to target an angular position.
     * @param current The current position of the motor.
     * @param target The target angle of the motor.
     */
    public void angle(Angle current, Angle target) {
        controlMode = () -> {
            angle(current, target);
        };

        
        if (encoder == null) {
            DriverStation.reportError("No assigned encoder, cannot control position", true);
        }

        double out = pid.calculate(current.getCWDeg(), target.getCWDeg());
        setPercent(out);
    }

    /**
     * Set the motor to 0% power. The motor will slowly wind down.
     */
    public void stop() {
        controlMode = () -> {
            setPercent(0);
        };

        setPercent(0);
    }

    /**
     * Set the motor to target a velocity of zero.
     */
    public void halt() {
        controlMode = () -> {
            velocity(Angle.cwDeg(0));
        };

        velocity(Angle.cwDeg(0));
    }

    /**
     * Set the motor to hold the current position. If it is moved, it will target the initial position.
     */
    public void hold() {
        controlMode = () -> {
            angle(holdAngle);
        };

        if (!isHolding) {
            holdAngle = currentAngle;
        }

        isHolding = true;
        angle(holdAngle);
    }

    @Override
    public void periodic() {
        currentAngle = encoder.getAngle();
        currentVelocity = encoder.getVelocity();

        controlMode.run();

        isHolding = false;
    }

    @Override
    public String toString() {
        return "Angle: " + currentAngle + "   Velocity: " + currentVelocity + " per second";
    }

}
