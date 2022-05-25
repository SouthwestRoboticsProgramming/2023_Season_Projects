package com.swrobotics.lib.motor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.MathUtil;

import edu.wpi.first.math.controller.PIDController;

public final class TalonMotor implements Motor {
    private static final int ENCODER_TICKS_PER_ROTATION = 2048;

    private enum RunState {
        PERCENT_OUTPUT,
        PID_POSITION,
        FEED_VELOCITY,
        HOLD
    }

    private final BaseTalon talon;
    private final PIDController pid;

    private RunState state;
    private double target;
    private double positionOffset;
    private double clampMin, clampMax;

    /**
     * Creates a new TalonMotor wrapping around a Talon motor
     * controller. The Talon's selected feedback sensor is used for
     * PID feedback.
     * 
     * @param talon Talon motor to wrap
     */
    public TalonMotor(BaseTalon talon) {
        this.talon = talon;
        pid = new PIDController(0, 0, 0);

        state = RunState.PERCENT_OUTPUT;
        target = 0;

        clampMin = -1;
        clampMax = 1;

        positionOffset = 0;
    }

    @Override
    public void update() {
        switch (state) {
            case PERCENT_OUTPUT:
                talon.set(ControlMode.PercentOutput, target);
                break;
            case PID_POSITION:
            case HOLD: {
                double output = pid.calculate(getPosition().getCWRad(), target);
                output = MathUtil.clamp(output, clampMin, clampMax);
                talon.set(ControlMode.PercentOutput, output);
                break;
            }
            case FEED_VELOCITY: {
                double output = pid.calculate(getVelocity(), target);
                output = MathUtil.clamp(output, clampMin, clampMax);
                talon.set(ControlMode.PercentOutput, output);
                break;
            }
        }
    }

    private double getRawPosition() {
        return talon.getSelectedSensorPosition() / ENCODER_TICKS_PER_ROTATION * Math.PI * 2;
    }

    @Override
    public Angle getPosition() {
        return Angle.cwRad(getRawPosition() - positionOffset);
    }

    @Override
    public void zeroPosition() {
        positionOffset = getRawPosition();
    }

    @Override
    public void setPosition(Angle position) {
        positionOffset = getRawPosition() - position.getCWRad();
    }

    @Override
    public double getVelocity() {
        return talon.getSelectedSensorVelocity() / ENCODER_TICKS_PER_ROTATION * 10 * 60;
    }

    @Override
    public void runAtPercentPower(double power) {
        state = RunState.PERCENT_OUTPUT;
        target = power;
    }

    @Override
    public void targetPosition(double position) {
        state = RunState.PID_POSITION;
        target = position;
        resetPID();
    }

    @Override
    public void runAtRPM(double rpm) {
        state = RunState.FEED_VELOCITY;
        target = rpm;
        resetPID();
    }

    @Override
    public void stop() {
        runAtPercentPower(0);
    }

    @Override
    public void halt() {
        runAtRPM(0);
    }

    @Override
    public void hold() {
        // Maintain current hold target if already holding
        if (state == RunState.HOLD)
            return;

        
        state = RunState.HOLD;
        target = getPosition().getCWRad();
        resetPID();
    }

    @Override
    public void setPID(double kP, double kI, double kD) {
        pid.setPID(kP, kI, kD);
    }

    @Override
    public void resetPID() {
        pid.reset();
    }

    @Override
    public void setOutputClamp(double min, double max) {
        clampMin = min;
        clampMax = max;
    }
}
