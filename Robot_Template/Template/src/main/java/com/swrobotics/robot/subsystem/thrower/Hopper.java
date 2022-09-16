package com.swrobotics.robot.subsystem.thrower;

import com.swrobotics.robot.Constants;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

public class Hopper implements Subsystem {
    private static final int INDEX_MOTOR_ID = 12;

    private static final NTDouble INDEX_KP = new NTDouble("Thrower/Hopper/Index/kP", 0.07);
    private static final NTDouble INDEX_KI = new NTDouble("Thrower/Hopper/Index/kI", 8.0);
    private static final NTDouble INDEX_KD = new NTDouble("Thrower/Hopper/Index/kD", 0.005);

    private static final NTDouble INDEX_IDLE_SPEED_DEGREES = new NTDouble("Thrower/Hopper/Index/Idle_Speed_DpS", 750);

    private final BallDetector ballDetector;
    private final TalonFXMotor indexMotor;

    private boolean isThrowing = false;

    public Hopper(BallDetector ballDetector) {
        this.ballDetector = ballDetector;

        indexMotor = new TalonFXMotor(this, INDEX_MOTOR_ID, Constants.CANIVORE);
        indexMotor.setPIDCalculators(INDEX_KP, INDEX_KI, INDEX_KD);
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

    /**
     * Call every periodic that the indexer should be speeding
     * up a ball into the thrower flywheel.
     * 
     * @param velocity Velocity to accelerate the indexing wheel to.
     */
    public void throwAtVelocity(Angle velocity) {
        isThrowing = true;
        indexMotor.velocity(velocity);
    }
    
    @Override
    public void periodic() {
        if (!isThrowing) {
            if (!ballDetector.isBallDetected()) {
                indexMotor.velocity(Angle.cwRot(INDEX_IDLE_SPEED_DEGREES.get()));
            } else {
                indexMotor.hold();
            }
        }
    }
}
