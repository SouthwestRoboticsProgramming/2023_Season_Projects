package com.swrobotics.lib.motor.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.encoder.filters.JumpToZeroFilter;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.Motor;
import com.swrobotics.lib.motor.ctre.NeutralMode;
import com.swrobotics.lib.schedule.Subsystem;
import com.revrobotics.CANSparkMax.IdleMode;

/**
 * Represents a motor controlled by a Spark MAX, such as a NEO.
 */
public abstract class SparkMaxMotor extends Motor {
    private final CANSparkMax spark;
    private final InternalEncoder internalEncoder;

    private static final class InternalEncoder extends Encoder {
        private static final double VELOCITY_TIME_SCALE = 1 / 60.0;

        private final RelativeEncoder encoder;

        public InternalEncoder(RelativeEncoder encoder) {
            this.encoder = encoder;
        }

        @Override
        protected Angle getRawAngleImpl() {
            return Angle.ccwRot(encoder.getPosition());
        }

        @Override
        protected Angle getVelocityImpl() {
            return Angle.ccwRot(encoder.getVelocity() * VELOCITY_TIME_SCALE);
        }
    }

    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * 
     * @param parent parent subsystem
     * @param canID CAN id of the Spark MAX
     * @param type type of motor connected to the Spark MAX
     */
    public SparkMaxMotor(Subsystem parent, int canID, MotorType type) {
        super(parent);
        spark = new CANSparkMax(canID, type);

        internalEncoder = new InternalEncoder(spark.getEncoder());
        setInternalEncoder();
    }

    @Override
    public void setPercentOutInternal(double percent) {
        spark.set(percent);
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
        setEncoder(internalEncoder);
        setEncoderFilter(new JumpToZeroFilter());
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        spark.setIdleMode(neutralMode == com.swrobotics.lib.motor.ctre.NeutralMode.BRAKE ? IdleMode.kBrake : IdleMode.kCoast);
    }
}
