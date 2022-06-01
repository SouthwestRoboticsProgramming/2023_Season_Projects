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
import com.swrobotics.lib.routines.Routine;


// public class TalonMotor implements Motor {

//     private static final int SENSOR_TICKS_PER_ROTATION = 2048;

//     // TODO: Rework this class to more clearly use the correct control modes

//     private final BaseTalon talon;
//     private final ProfiledPIDController pid;
//     private final SimpleMotorFeedforward feed;
//     private final BangBangController bang;

//     private AbsoluteEncoder encoder;
//     private boolean useEncoder;
    
//     private double setpoint;
//     private boolean isFlywheel;

//     private Angle sensorOffset;
//     private MotorMode mode;

//     /**
//      * Creates a TalonMotor wrapping around another motor controller,
//      * giving it additional functionality
//      * @param talon Talon SRX, SPX, FX to wrap
//      * @param motorType Type of motor that the controller is attached to
//      */
//     public TalonMotor(BaseTalon talon, MotorType motorType) {
//         this.talon = talon;

//         double maxVelocity = motorType.getMaxRPM();
//         double maxAcceleration = motorType.getAcceleration();

//         pid = new ProfiledPIDController(0, 0, 0, new TrapezoidProfile.Constraints(maxVelocity, maxAcceleration));
//         feed = new SimpleMotorFeedforward(0, 0, 0);
//         bang = new BangBangController();

//         isFlywheel = false;

//         sensorOffset = Angle.cwDeg(0);
//     }

//     public TalonMotor(BaseTalon talon) {
//         this(talon, MotorType.CIM);
//     }

//     // TODO: Store the offset in a variable, don't let the motors control it.

//     @Override
//     public void set(MotorMode mode, double demand) {
//         switch (mode) {
//             case PERCENT_OUT:
//             talon.set(ControlMode.PercentOutput, demand);
//             break;
            
//             case POSITION:
//             setpoint = demand;
//                 mode_position();
//                 break;
            
//                 case VELOCITY:
//                 setpoint = demand;
//                 mode_velocity();
//                 break;
                
//                 case STOP:
//                 talon.set(ControlMode.PercentOutput, 0);
//                 break;
                
//                 case HALT:
//                 setpoint = 0;
//                 mode_velocity();
//                 break;
                
//                 case HOLD:
//                 if (this.mode != MotorMode.HOLD) {
//                     setpoint = getPosition().getCWDeg();
//                 }
//                 mode_position();
                
//                 break;
                
//                 default:
//                 break;
//             }
//             this.mode = mode;
//         }

//     private void mode_position() {
//         if (pid.getP() == 0.0 && pid.getI() == 0.0) {
//             throw new Error("No PID gains set");
//         }

//         double position = getPosition().getCWDeg();
//         double out = pid.calculate(position, setpoint);
//         talon.set(ControlMode.PercentOutput, out);
//     }

//     private void mode_velocity() {
//         double velocity = getVelocity();

//         double out = feed.calculate(setpoint);
//         if (isFlywheel) {
//             out = bang.calculate(velocity, setpoint) + 0.9 * out;
//         }

//         talon.set(ControlMode.PercentOutput, out);
//     }






//     /**
//      * Determines the velocity control of the motor
//      * @param isFlywheel If the motor is controlling a flywheel
//      */
//     public void setFlywheelMode(boolean isFlywheel) {
//         this.isFlywheel = isFlywheel;
//     }

//     @Override
//     public Angle getPosition() {
//         double position = talon.getSelectedSensorPosition() / SENSOR_TICKS_PER_ROTATION;
//         if (useEncoder) {
//             position = encoder.getAbsoluteAngle().getCWDeg();
//         }

//         return Angle.cwDeg(position);
//     }

//     @Override
//     public double getVelocity() {
//         double velocity = talon.getSelectedSensorVelocity() * 10 * 60;
//         if (useEncoder) {
//             velocity = encoder.getRPM();
//         }
//         return velocity;
//     }

//     @Override
//     public void setAbsoluteSensor(AbsoluteEncoder encoder) {
//         useEncoder = true;
//         this.encoder = encoder; // TODO: Change off CANCoder
        
//     }

//     @Override
//     public void setPosition(Angle position) {
//         talon.setSelectedSensorPosition(position.getCWDeg() * SENSOR_TICKS_PER_ROTATION); // FIXME Not ticks per rotation
//         if (useEncoder) {
//             encoder.setPosition(position);
//         }
        
//     }

//     @Override
//     public void zeroPosition() {
//         sensorOffset.setCWDeg(sensorOffset.getCWDeg() - getPosition().getCWDeg());
//     }

//     @Override
//     public void setPID(double kP, double kI, double kD) {
//         pid.setPID(kP, kI, kD);
        
//     }

// }



public class TalonMotor implements Motor, Routine {
    
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
        // TODO Auto-generated method stub
        
    }

    public void set(MotorMode mode, Angle demand) {
        // TODO Auto-generated method stub
    }


    // Sensor management

    @Override
    public Angle getPosition() {
        return rawPosition.sub(offset);
    }
    
    @Override
    public void setPosition(Angle position) {
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

        if (encoder != null) {
            rawPosition = encoder.getRawAngle().sub(offset); // Raw angle because I'm doing the offset
            velocity = encoder.getVelocity();
        
        
        } else {
            rawPosition = Angle.cwDeg(talon.getSelectedSensorPosition() / sensorCoefficient * 360);
            velocity = Angle.cwDeg(talon.getSelectedSensorVelocity() / sensorCoefficient /* Divide by other stuff TODO */);
        }
    }

}