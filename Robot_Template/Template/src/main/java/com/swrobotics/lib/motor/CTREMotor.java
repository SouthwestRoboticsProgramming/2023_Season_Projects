package com.swrobotics.lib.motor;

import java.util.ResourceBundle.Control;

import javax.swing.text.Position;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.MotorSafety;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.MotorMode;

public class CTREMotor implements NewMotor { // TODO: Implement subystem

    private static final int SENSOR_TICKS_PER_ROTATION = 2048;

    private final BaseTalon talon;
    private final ProfiledPIDController pid;
    private final SimpleMotorFeedforward feed;
    private final BangBangController bang;

    private CANCoder encoder;
    
    private double setpoint;
    private boolean isFlywheel;

    private Angle sensorOffset;

    /**
     * Creates a TalonMotor waprring around another motor controller,
     * giving it additional functionality
     * @param talon Talon SRX, SPX, FX to wrap
     * @param motorType Type of motor that the controller is attached to
     */
    public CTREMotor(BaseTalon talon, MotorType motorType) {
        this.talon = talon;

        double maxVelocity = motorType.getMaxRPM();
        double maxAcceleration = motorType.getAcceleration();

        pid = new ProfiledPIDController(0, 0, 0, new TrapezoidProfile.Constraints(maxVelocity, maxAcceleration));
        feed = new SimpleMotorFeedforward(0, 0, 0);
        bang = new BangBangController();

        encoder = null;
        isFlywheel = false;

        sensorOffset = 0;
    }

    // TODO: Store the offset in a variable, don't let the motors control it.

    @Override
    public void set(MotorMode mode, double demand) {
        switch (mode) {
            case PERCENT_OUT:
                talon.set(ControlMode.PercentOutput, demand);
                break;
            
            case POSITION:
                setpoint = demand;
                mode_position();
                break;
            
            case VELOCITY:
                setpoint = demand;
                mode_velocity();
                break;

            case STOP:
                talon.set(ControlMode.PercentOutput, 0);
                break;
            
            case HALT:
                setpoint = 0;
                mode_velocity();
                break;
            
            case HOLD:
                setpoint = getPosition().getCWDeg(); // TODO: If the motor moves, move back to where it was
                mode_position();

                break;
        
            default:
                break;
        }
    }

    private void mode_position() {
        if (pid.getP() == 0 && pid.getI() == 0) {
            throw new Error("No PID gains set");
        }

        double position = getPosition().getCWDeg();
        double out = pid.calculate(position, setpoint);
        talon.set(ControlMode.PercentOutput, out);
    }

    private void mode_velocity() {
        double velocity = getVelocity();

        double out = feed.calculate(setpoint);
        if (isFlywheel) {
            out = bang.calculate(velocity, setpoint) + 0.9 * out;
        }

        talon.set(ControlMode.PercentOutput, out);
    }






    /**
     * Determines the velocity control of the motor
     * @param isFlywheel
     */
    public void setFlywheelMode(boolean isFlywheel) {
        this.isFlywheel = isFlywheel;
    }

    @Override
    public Angle getPosition() {
        double position = talon.getSelectedSensorPosition() / SENSOR_TICKS_PER_ROTATION;
        if (encoder != null) {
            position = encoder.getAbsolutePosition();
        }

        return Angle.cwDeg(position);
    }

    @Override
    public double getVelocity() {
        double velocity = talon.getSelectedSensorVelocity() * 10 * 60;
        if (encoder != null) {
            velocity = encoder.getVelocity();
        }
        return velocity;
    }

    @Override
    public void setAbsoluteSensor(CANCoder encoder) {
        this.encoder = encoder; // TODO: Change off CANCoder
        
    }

    @Override
    public void setPosition(Angle position) {
        talon.setSelectedSensorPosition(position.getCWDeg() * SENSOR_TICKS_PER_ROTATION); // FIXME Not ticks per rotation
        if (encoder != null) {
            encoder.setPosition(position.getCWDeg());
        }
        
    }

    @Override
    public void zeroPosition() {
        sensorOffset.setCWDeg(sensorOffset.getCWDeg() - getPosition().getCWDeg());
    }

    @Override
    public void setPID(double kP, double kI, double kD) {
        pid.setPID(kP, kI, kD);
        
    }

    @Override
    public void resetPID() {
        return;
        
    }

    @Override
    public void periodic() {
        // TODO Auto-generated method stub
        
    }

}
