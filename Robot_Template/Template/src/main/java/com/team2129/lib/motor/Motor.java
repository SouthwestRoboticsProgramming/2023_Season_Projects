package com.team2129.lib.motor;

import java.util.function.Supplier;

import com.team2129.lib.encoder.Encoder;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.MathUtil;
import com.team2129.lib.motor.calc.PIDPositionCalculator;
import com.team2129.lib.motor.calc.PIDVelocityCalculator;
import com.team2129.lib.motor.calc.PositionCalculator;
import com.team2129.lib.motor.calc.VelocityCalculator;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.utils.InputUtils;

/**
 * Represents a generic motor. This class is intended to provide
 * a common interface between various types of motors, to make it
 * easier to swap motors and to learn how to program them.
 */
public abstract class Motor implements Subsystem {
    private enum ControlMode {
        PERCENT(false),
        POSITION(true),
        VELOCITY(false),
        HOLD(false);

        final boolean isPosition;

        ControlMode(boolean isPosition) {
            this.isPosition = isPosition;
        }
    }

    private PositionCalculator positionCalc;
    private VelocityCalculator velocityCalc;
    private Encoder encoder;
    private ControlMode controlMode;
    private Runnable controlModeImpl;

    private Angle holdTarget;
    
    private boolean inverted;
    private double neutralDeadband;

    /**
     * Creates a new {@code Motor} instance that belongs to a specified {@link Subsystem}.
     * 
     * @param parent parent subsystem
     */
    public Motor(Subsystem parent) {
        Scheduler.get().addSubsystem(parent, this);

        positionCalc = null;
        velocityCalc = null;
        encoder = null;

        inverted = false;
        neutralDeadband = 0.01;

        percent(0);
    }

    /**
     * Sets the percent output power of the motor. A negative percent
     * value will cause the motor to turn in the opposite direction.
     * 
     * @param percent percent output from -1 to 1
     */
    public void percent(double percent) {
        controlMode = ControlMode.PERCENT;
        controlModeImpl = () -> percentImpl(percent);
    }

    /**
     * Tells the motor to target a specific encoder measurement.
     * The actual motor output is calculated by the set {@link PositionCalculator}. This
     * function requires both a position calculator and encoder to be set.
     * 
     * @param angle
     * @throws IllegalStateException if no position calculator or encoder is set
     * 
     * @see Encoder
     * @see #setEncoder(Encoder)
     * @see #setPositionCalculator(PositionCalculator)
     */
    public void position(Angle angle) {
        if (encoder == null)
            throw new IllegalStateException("Cannot set position, no encoder is set");

        position(encoder::getAngle, angle);
    }

    /**
     * Tells the motor to target a specific encoder measurement.
     * The actual motor output is calculated by the set {@link PositionCalculator}. This
     * function requires a position calculator to be set.
     * 
     * @param angle
     * @throws IllegalStateException if no position calculator or encoder is set
     * 
     * @see Encoder
     * @see #setEncoder(Encoder)
     * @see #setPositionCalculator(PositionCalculator)
     */
    public void position(Supplier<Angle> angleGetter, Angle angle) {
        if (positionCalc == null)
            throw new IllegalStateException("Cannot set position, no position calculator is set");
        
        if (!controlMode.isPosition)
            positionCalc.reset();
        controlMode = ControlMode.POSITION;
        controlModeImpl = () -> positionImpl(angleGetter, angle);
    }

    /**
     * Tells the motor to target a specific rotational velocity.
     * The actual motor output is calculated by the set {@link VelocityCalculator}. This
     * function requires both a velocity calculator and encoder to be set.
     * 
     * @param velocity target velocity in angle per second
     * @throws IllegalStateException if no velocity calculator or encoder is set
     * 
     * @see Encoder
     * @see #setEncoder(Encoder)
     * @see #setVelocityCalculator(VelocityCalculator)
     */
    public void velocity(Angle velocity) {
        if (encoder == null)
            throw new IllegalStateException("Cannot set position, no encoder is set");

        velocity(encoder::getAngle, velocity);
    }

    /**
     * Tells the motor to target a specific rotational velocity.
     * The actual motor output is calculated by the set {@link VelocityCalculator}. This
     * function requires a velocity calculator to be set.
     * 
     * @param angleGetter getter for the current angle
     * @param velocity target velocity in angle per second
     * @throws IllegalStateException if no velocity calculator or encoder is set
     * 
     * @see Encoder
     * @see #setEncoder(Encoder)
     * @see #setVelocityCalculator(VelocityCalculator)
     */
    public void velocity(Supplier<Angle> angleGetter, Angle velocity) {
        if (velocityCalc == null)
            throw new IllegalStateException("Cannot set velocity, no velocity calculator is set");

        if (controlMode != ControlMode.VELOCITY)
            velocityCalc.reset();
        controlMode = ControlMode.VELOCITY;
        controlModeImpl = () -> velocityImpl(angleGetter, velocity);
    }

    /**
     * Stops the motor by setting its percent output to zero. This
     * completely disables the motor output.
     */
    public void stop() {
        percent(0);
    }

    /**
     * Stops the motor by setting its target velocity to zero. The motor
     * will actively try to keep the output still, but is not guaranteed to
     * stay in the same position.
     */
    public void halt() { velocity(Angle.zero()); }
    
    /**
     * Stops the motor by continuously targeting its current position. The
     * motor will actively adjust its position, and will correct for any changes
     * in position while it is held.
     */
    public void hold() {
        if (positionCalc == null)
            throw new IllegalStateException("Cannot set position, no position calculator is set");

        if (encoder == null)
            throw new IllegalStateException("Cannot set position, no encoder is set");

        if (!controlMode.isPosition)
            positionCalc.reset();
        
        if (controlMode != ControlMode.HOLD)
            holdTarget = encoder.getAngle();

        controlModeImpl = () -> positionImpl(encoder::getAngle, holdTarget);
    }

    /**
     * Gets the currently assigned encoder for this motor. This will
     * be the motor or motor controller's internal encoder by default 
     * if one is present, or {@code null} if no encoder is assigned.
     * 
     * @return currently assigned encoder
     */
    public Encoder getEncoder() {
        return encoder;
    }

    /**
     * Assigns a different encoder to this motor for feedback control.
     * 
     * @param encoder encoder to assign
     */
    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Convenience method to set both the position calculator and velocity
     * calculator to use a PID controller with the specified coefficients.
     * 
     * @param kP proportional coefficient
     * @param kI integral coefficient
     * @param kD derivative coefficient
     */
    public void setPIDCalculators(double kP, double kI, double kD) {
        positionCalc = new PIDPositionCalculator(kP, kI, kD);
        velocityCalc = new PIDVelocityCalculator(kP, kI, kD);
    }

    /**
     * Convenience method to set both the position calculator and velocity
     * calculator to use a PID controller with the specified coefficients.
     * This method allows them to read their coefficients from NetworkTables,
     * and they will be automatically updated if the NetworkTables entry changes.
     * 
     * @param kP proportional coefficient entry
     * @param kI integral coefficient entry
     * @param kD derivative coefficient entry
     */
    public void setPIDCalculators(NTDouble kP, NTDouble kI, NTDouble kD) {
        positionCalc = new PIDPositionCalculator(kP, kI, kD);
        velocityCalc = new PIDVelocityCalculator(kP, kI, kD);
    }

    /**
     * Gets the currently assigned position calculator. Will return
     * {@code null} if no position calculator is set.
     * 
     * @return currently assigned position calculator
     * @see #setPositionCalculator(PositionCalculator)
     */
    public PositionCalculator getPositionCalculator() {
        return positionCalc;
    }

    /**
     * Assigns a different position calculator to this motor. This
     * calculator will be used to calculate motor output in the
     * {@code position()} and {@code hold()} control modes.
     * 
     * @param calc position calculator to assign
     * @see #position(Angle)
     * @see #halt()
     */
    public void setPositionCalculator(PositionCalculator calc) {
        this.positionCalc = calc;
    }

    /**
     * Gets the currently assigned velocity calculator. Will return
     * {@code null} if no velocity calculator is set.
     * 
     * @return currently assigned velocity calculator
     * @see #setVelocityCalculator(VelocityCalculator)
     */
    public VelocityCalculator getVelocityCalculator() {
        return velocityCalc;
    }

    /**
     * Assigns a different velocity calculator to this motor. This
     * calculator will be used to calculate motor output in the
     * {@code velocity()} and {@code halt()} control modes.
     * 
     * @param calc velocity calculator to assign
     * @see #velocity(Angle)
     * @see #halt()
     */
    public void setVelocityCalculator(VelocityCalculator calc) {
        this.velocityCalc = calc;
    }

    /**
     * Gets whether the motor's output is currenty inverted.
     * 
     * @return inverted
     */
    public boolean isInverted() {
        return inverted;
    }

    /**
     * Sets whether the motor's output should be inverted.
     * 
     * @param inverted whether to invert output
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    /**
     * Gets the currently set neutral deadband.
     * 
     * @return neutral deadband
     */
    public double getNeutralDeadband() {
        return neutralDeadband;
    }

    /**
     * Sets the range at which the motor will assume the percent output
     * is neutral. If the percent output is within the deadband, it will
     * be clamped to zero.
     * 
     * @param neutralDeadband new neutral deadband
     */
    public void setNeutralDeadband(double neutralDeadband) {
        this.neutralDeadband = neutralDeadband;
    }

    /**
     * Actually sets the motor's percent output. This should be implemented
     * by all motor types to be able to control them. A percent output of 1
     * should be full speed clockwise, a percent output of -1 should be full
     * speed counterclockwise, and a percent output of 0 should be stopped.
     * The provided percent output will never exceed this range.
     * 
     * @param percent percent output from -1 to 1
     */
    protected abstract void setPercentOutInternal(double percent);

    private void setPercentOutFiltered(double percent) {
        if (inverted)
            percent = -percent;

        percent = InputUtils.applyDeadband(percent, neutralDeadband);

        setPercentOutInternal(MathUtil.clamp(percent, -1, 1));
    }

    // Implementation of percent output control mode
    private void percentImpl(double percent) {
        setPercentOutFiltered(percent);
    }

    // Implementation of position control mode (used for position() and hold())
    private void positionImpl(Supplier<Angle> angleGetter, Angle angle) {
        setPercentOutFiltered(positionCalc.calculate(angleGetter.get(), angle));
    }

    // Implementation of velocity control mode (used for velocity() and halt())
    private void velocityImpl(Supplier<Angle> velocityGetter, Angle velocity) {
        setPercentOutFiltered(velocityCalc.calculate(velocityGetter.get(), velocity));
    }

    @Override
    public void periodic() {
        // Run the currently set control mode implementation
        controlModeImpl.run();
    }
}
