package com.swrobotics.lib.motor;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.routine.Routine;

import edu.wpi.first.wpilibj.DriverStation;

import com.swrobotics.lib.encoder.Encoder;

public abstract class Motor extends Routine {

    private Encoder encoder;

    private MotorMode mode;
    private double demand; // CW Degrees/s, CW Degrees, percent out

    private Angle holdAngle; // Recorded to save the angle for the HOLD mode
    private MotorMode lastMode; // Record last mode to make HOLD work properly


    /**
     * Create a motor with a defined encoder. This encoder can be either internal or external but
     * it must be defined and controlled outside of this motor.
     * @param encoder
     */
    public Motor(Encoder encoder) {
        this.encoder = encoder;
        mode = MotorMode.STOP;
    }

    /**
     * Create a motor without a defined encoder.
     * This is intended for motors without an integrated encoder. <br></br>
     * If your motor has an integrated encoder, create an InternalEncoder implementation.
     */
    public Motor() {
        mode = MotorMode.STOP;
    }


    /**
     * Gives the motor an absolute encoder
     * @param encoder Encoder implementation
     */
    public void assignEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public void set(MotorMode mode, Angle demand) {
        this.mode = mode;
        this.demand = demand.getCWDeg();
    }

    /**
     * Specify what the motor should be doing. This implementation is for
     * @param mode
     * @param demand
     */
    public void set(MotorMode mode, double demand) {
        this.mode = mode;
        this.demand = demand;
    }

    public void set(MotorMode mode) {
        if (mode != MotorMode.HOLD && mode != MotorMode.HALT && mode != MotorMode.HOLD) {
            DriverStation.reportError("Demand is not a stop mode, defaulting to a " + mode + " of zero", true);
        }
        this.mode = mode;
        demand = 0;
    }

    public Encoder getEncoder() {
        return encoder;
    }


    /**
     * Give the motor a percentage of the voltage recieved.
     * @param percent The demanded percent out of the motor.
     */
    protected abstract void percent(double percent);

    /**
     * Control the motor to spin at a specified speed.
     * @param current The current velocity of the motor.
     * @param target The target velocity of the motor.
     */
    protected abstract void velocity(Angle current, Angle target);

    /**
     * Control the motor to target an angular position.
     * @param current The current angle of the motor.
     * @param target The target angle of the motor.
     */
    protected abstract void angle(Angle current, Angle target);

    @Override
    public void periodic() {

        if (encoder == null) {
            // If there isn't an encoder, don't do anything
            if (mode != MotorMode.PERCENT_OUT) {
                DriverStation.reportError("No encoder, cannot use mode: " + mode, true);
                demand = 0;
            }
            percent(demand);
            return;
        }

        switch (mode) {
            case PERCENT_OUT:
                percent(demand);
                break;
            
            case ANGLE:
                angle(encoder.getAngle(), Angle.cwDeg(demand));
                break;

            case VELOCITY:
                velocity(encoder.getVelocity(), Angle.cwDeg(demand));
                break;

            case STOP:
                percent(0);
                break;

            case HALT:
                velocity(encoder.getVelocity(),Angle.cwDeg(0));
                break;

            case HOLD:
                if (mode != lastMode) {
                    holdAngle = encoder.getAngle();
                }
                angle(encoder.getAngle(), holdAngle);
                break;
        
            default:
                DriverStation.reportError("Hmm, that should never happen, no motor mode defined", true);
                break;
        }
    }




}
