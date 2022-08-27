package com.team2129.lib.motor.ctre;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.team2129.lib.encoder.Encoder;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.Motor;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Represents a CTRE Talon motor. This class is intended to be
 * extended to implement any unsupported Talon motor types.
 */
public abstract class TalonMotor extends Motor {
    private static final int ENCODER_VELOCITY_TIME_SCALE = 10;

    protected final BaseTalon talon;
    private final InternalEncoder internalEncoder;

    private final class InternalEncoder extends Encoder {
        private final double encoderTicksPerRotation;

        public InternalEncoder(double encoderTicksPerRotation) {
            this.encoderTicksPerRotation = encoderTicksPerRotation;
        }

        @Override
        protected Angle getRawAngleImpl() {
            try {
                return Angle.ccwRot(talon.getSelectedSensorPosition() / encoderTicksPerRotation);
            } catch (ArithmeticException e) {
                DriverStation.reportError("Encoder ticks per rotation must not be 0", true);
                return Angle.zero();
            }
        }

        @Override
        protected Angle getVelocityImpl() {
            try {
                return Angle.ccwRot(talon.getSelectedSensorVelocity() / encoderTicksPerRotation * ENCODER_VELOCITY_TIME_SCALE);
            } catch (Exception e) {
                DriverStation.reportError("Encoder ticks per rotation must not be 0", true);
                return Angle.zero();
            }
        }
    }
    
    /**
     * Creates a new instance that belongs to a specified subsystem.
     * The specified {@code BaseTalon} is wrapped by this instance.
     * If the encoder ticks per rotation is less than or equal to zero,
     * it will be assumed that there is no internal encoder.
     * 
     * @param parent parent subsystem
     * @param talon BaseTalon to wrap
     * @param encoderTicksPerRotation number of encoder ticks per rotation of the output shaft
     */
    public TalonMotor(Subsystem parent, BaseTalon talon, double encoderTicksPerRotation) {
        super(parent);
        this.talon = talon;

        // Configure neutral deadband on motor so 0 percent output is neutral
        talon.configNeutralDeadband(Double.MIN_VALUE);

        if (encoderTicksPerRotation > 0) {
            internalEncoder = new InternalEncoder(encoderTicksPerRotation);
            setInternalEncoder();
        } else {
            internalEncoder = null;
        }
    }

    @Override
    public void setPercentOutInternal(double percent) {
        talon.set(ControlMode.PercentOutput, percent);
    }

    /**
     * Gets the internal encoder of this motor.
     *
     * @return internal encoder
     */
    public Encoder getInternalEncoder() {
        return internalEncoder;
    }

    /**
     * Sets this motor to use its internal encoder.
     */
    public void setInternalEncoder() {
        if (internalEncoder == null)
            throw new IllegalStateException("No internal encoder - did you specify ticks per rotation?");
        setEncoder(internalEncoder);
    }

    /**
     * Sets the motor's neutral mode to the specified mode.
     * 
     * @param neutralMode new neutral mode
     */
    public void setNeutralMode(com.team2129.lib.motor.ctre.NeutralMode neutralMode) {
        talon.setNeutralMode(neutralMode == com.team2129.lib.motor.ctre.NeutralMode.BRAKE ? NeutralMode.Brake : NeutralMode.Coast);
    }
}
