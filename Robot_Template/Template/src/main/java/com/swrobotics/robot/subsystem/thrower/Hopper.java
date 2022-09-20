package com.swrobotics.robot.subsystem.thrower;

import com.swrobotics.robot.Constants;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

public class Hopper implements Subsystem {
    private static final int INDEX_MOTOR_ID = 12;

    private static final NTDouble INDEX_IDLE_SPEED_PERCENT = new NTDouble("Thrower/Hopper/Index/Idle_Speed_Percent", 0.1);

    private final BallDetector ballDetector;
    private final TalonFXMotor indexMotor;

    private boolean isThrowing = false;

    public Hopper(BallDetector ballDetector) {
        this.ballDetector = ballDetector;

        indexMotor = new TalonFXMotor(this, INDEX_MOTOR_ID, Constants.CANIVORE);
        indexMotor.setInverted(true);
        indexMotor.setNeutralMode(NeutralMode.BRAKE);

        // There is a possibility that this motor needs to be inverted
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

    public void setIndexPercent(double percent) {
        indexMotor.percent(percent);
    }
    
    @Override
    public void periodic() {
        if (!isThrowing) {
            if (!ballDetector.isBallDetected()) {
                indexMotor.percent(INDEX_IDLE_SPEED_PERCENT.get());
            } else {
                indexMotor.stop();
            }
        }
    }
}
