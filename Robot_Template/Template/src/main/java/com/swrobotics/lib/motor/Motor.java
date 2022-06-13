package com.swrobotics.lib.motor;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.routine.Routine;

import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;

import com.swrobotics.lib.encoder.Encoder;

/**
 * An abstract class to put all motor functions into one interface for better motor controlls and uniform code across all vendors.
 */
public abstract class Motor extends Routine {

    private ProfiledPIDController pid;
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
     * Create a motor with a defined encoder. This encoder can be either internal or external but
     * it must be defined and controlled outside of this motor.
     * @param encoder
     */
    public Motor(Encoder encoder) {
        this.encoder = encoder;

        controlMode = () -> {
            return;
        };

        pid = new ProfiledPIDController(0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(Double.MAX_VALUE, Double.MAX_VALUE));
        feed = new SimpleMotorFeedforward(0.0, 0.0);
        bang = new BangBangController();
    }

    /**
     * Create a motor without a defined encoder.
     * This is intended for motors without an integrated encoder. <br></br>
     * If your motor has an integrated encoder, create an InternalEncoder implementation.
     */
    public Motor() {
        this(null);
    }

    /**
     * Set the profiled PID controller for position control with the motor.
     * @param pid A configured PID controller.
     */
    public void setPIDController(ProfiledPIDController pid) {
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

    }

    /**
     * Actually set the motor output. This is the implementation for motor usage.
     * 
     * @param percent Demanded percent -1 - 1 output for the motor.
     */
    protected abstract void setPercent(double percent);

    /**
     * Control the motor to spin at a specified speed.
     * @param current The current velocity of the motor.
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
            out = pid.calculate(currentVelocity.getCWDeg(), target.getCWDeg())  +  feed.calculate(target.getCWDeg() * 0.9);
        }

        setPercent(out);
    }

    /**
     * Control the motor to target an angular position.
     * @param current The current angle of the motor.
     * @param target The target angle of the motor.
     */
    public void angle(Angle target) {
        controlMode = () -> {
            angle(target);
        };


        if (encoder == null) {
            DriverStation.reportError("No assigned encoder, cannot control position", true);
        }

        double out = pid.calculate(currentAngle.getCWDeg(), target.getCWDeg());
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
