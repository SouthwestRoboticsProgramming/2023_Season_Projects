package com.swrobotics.lib.motor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.routine.Routine;


public class TalonMotor extends Routine implements Motor {
    
    public static final double sensorCoefficient = 2048.0;
    /*
    TODO:
    - All sensor stuff
    - Motor modes with adjustable method
    - Change all RPMs to Angle / Second
    */

    // Angles
    private Angle offset; // Subtracted to get real position
    private Angle rawPosition;
    private Angle velocity;

    // Motor and sensor
    private final BaseTalon talon;
    private Encoder encoder;

    // Controllers
    private final BangBangController bang;
    private final ProfiledPIDController pid;
    private SimpleMotorFeedforward feed; // Not final for recreation

    // Closed loop control
    private boolean useClosedLoop;
    private boolean useFeedforward;

    // Mode and demand
    private MotorMode mode;
    private MotorMode lastMode;

    // Demand for what is wanted
    private double demand;
    private Angle angleDemand;

    private Angle holdPosition;

    /**
     * Create a talon motor without closed loop control. This is intended for very basic use cases where voltae control is all you need.
     * @param talon CTRE motor to wrap.
     */
    public TalonMotor(BaseTalon talon) {
        this.talon = talon;
        useClosedLoop = false;
        useFeedforward = false;

        // Don't create the controllers
        pid = null;
        feed = null;
        bang = null;

    }

    /**
     * Create a talon motor without closed loop control but with open loop. If your feedforward is configured correctly, the motor can estimate a voltage to maintain a velocity.
     * @param talon CTRE motor to wrap.
     * @param feed A configured feedforward controller.
     */
    public TalonMotor(BaseTalon talon, SimpleMotorFeedforward feed) {
        this.talon = talon;
        this.feed = feed;

        useClosedLoop = false;
        useFeedforward = true;

        pid = null;
        bang = null;
    }

    /**
     * Create a talon motor with closed loop control but without open loop. Enables position targeting.
     * @param talon CTRE motor to wrap.
     * @param pid A congigured profiled PID controller.
     */
    public TalonMotor(BaseTalon talon, ProfiledPIDController pid) {
        this.talon = talon;
        this.pid = pid;

        bang = new BangBangController();

        useClosedLoop = true;
        useFeedforward = false;

        feed = null;
    }

    /**
     * Create a talon motor with full functionality. Both closed and open loop control, ideal for intricate demands.
     * @param talon CTRE motor to wrap.
     * @param pid A configured profiled PID controller.
     * @param feed A configured feedforward controller.
     */
    public TalonMotor(BaseTalon talon, ProfiledPIDController pid, SimpleMotorFeedforward feed) {
        this.talon = talon;
        this.pid = pid;
        this.feed = feed;

        bang = new BangBangController();

        useClosedLoop = true;
        useFeedforward = true;
    }

    @Override
    public void set(MotorMode mode, double demand) {
        this.mode = mode;
        this.demand = demand;
        
    }

    public void set(MotorMode mode, Angle demand) {
        this.mode = mode;
        angleDemand = demand;
    }


    // Sensor management

    @Override
    public Angle getAngle() {
        return rawPosition.sub(offset);
    }
    
    @Override
    public void setEncoderPosition(Angle position) {
        offset = rawPosition.sub(position);   
    }
    
    @Override
    public void zeroPosition() {
        offset = rawPosition; 
    }

    @Override
    public Angle getVelocity() {
        return velocity;
    }


    public void setPID(double kP, double kI, double kD) {
        if (useClosedLoop) {
            pid.setPID(kP, kI, kD);
        } else {
            DriverStation.reportError("Motor " + talon.getDeviceID() + " constructed with no PID controller, cannot update PID", true);
        }
        
    }

    public void setPIDContraints(TrapezoidProfile.Constraints contraints) {
        if (useClosedLoop) {
            pid.setConstraints(contraints);
        } else {
            DriverStation.reportError("Motor " + talon.getDeviceID() + " constructed with no PID controller, cannot update PID constraints", true);
        }
    }

    public void recreateFeedforward(double ks, double kv, double ka) {
        if (feed != null) {
            feed = new SimpleMotorFeedforward(ks, kv, ka);
        } else {
            DriverStation.reportError("Motor " + talon.getDeviceID() + " constructed with no Feedforward controller, cannot update gains", true);
        }
    }

    @Override
    public void assignEncoder(Encoder encoder) {
        this.encoder = encoder;
        
    }


    @Override
    public void periodic() {

        // Update positions
        if (encoder != null) {
            rawPosition = encoder.getRawAngle().sub(offset); // Raw angle because I'm doing the offset
            velocity = encoder.getVelocity();
        
        
        } else {
            rawPosition = Angle.cwDeg(talon.getSelectedSensorPosition() / sensorCoefficient * 360);
            velocity = Angle.cwDeg(talon.getSelectedSensorVelocity() / sensorCoefficient /* Divide by other stuff TODO */);
        }



        switch (mode) {
            case PERCENT_OUT:
                talon.set(ControlMode.PercentOutput, demand);
                break;
        
            case POSITION:
                position(angleDemand);
                break;

            case VELOCITY:
                velocity(angleDemand);
                break;

            case STOP:
                talon.set(ControlMode.PercentOutput, 0);
                break;

            case HALT:
                velocity(Angle.cwRad(0));
                break;

            case HOLD:
                if (lastMode != MotorMode.HOLD) {
                    position(getAngle());
                    holdPosition = getAngle();
                } else {
                    position(holdPosition);
                }
                break;
            
            default:
                DriverStation.reportError("Hmmmm, that shouldn't have happened, not motor mode defined", true);
                break;
        }

        lastMode = mode;

    }


    private void velocity(Angle demand) {
        // TODO
    }

    private void position(Angle demand) {

    }

}