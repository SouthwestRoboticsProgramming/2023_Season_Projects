package com.swrobotics.robot.subsystem.thrower;

import com.swrobotics.robot.Constants;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

public class Hopper implements Subsystem {
    private static final int INDEX_MOTOR_ID = 12;

    private static final NTDouble INDEX_IDLE_SPEED_PERCENT = new NTDouble("Thrower/Hopper/Index/Idle Speed Percent", 0.1);

    private static final NTDouble L_INDEX_VELOCITY = new NTDouble("Thrower/Hopper/Index/Motor Velocity RPS", 2129);

    private final BallDetector ballDetector;
    private final TalonFXMotor indexMotor;

    private boolean controlOverride = false;

    public Hopper(BallDetector ballDetector) {
        this.ballDetector = ballDetector;

        indexMotor = new TalonFXMotor(this, INDEX_MOTOR_ID, Constants.CANIVORE);
        indexMotor.setInverted(true);
        indexMotor.setNeutralMode(NeutralMode.BRAKE);

        L_INDEX_VELOCITY.setTemporary();
    }

    public boolean isBallDetected() {
        return ballDetector.isBallDetected();
    }

    /**
     * Reads if the ball was there last tick and now isn't
     * @return
     */
    public boolean isBallGone() {
        return ballDetector.isBallGone();
    }

    public void turnOffControlOverride() {
        controlOverride = false;
    }

    public void setIndexPercent(double percent) {
        controlOverride = true;
        indexMotor.percent(percent);
    }
    
    @Override
    public void periodic() {
        if (!controlOverride) {
            if (!ballDetector.isBallDetected()) {
                indexMotor.percent(INDEX_IDLE_SPEED_PERCENT.get());
            } else {
                indexMotor.stop();
            }
        }

        L_INDEX_VELOCITY.set(indexMotor.getEncoder().getVelocity().getCWDeg() / 360);
    }
}
