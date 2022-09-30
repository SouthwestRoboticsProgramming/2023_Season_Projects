package com.swrobotics.robot.subsystem.thrower;

import edu.wpi.first.wpilibj.DigitalInput;

import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.schedule.Subsystem;

/**
 * A sensor to detect when a ball is engaged with the indexing wheel.
 */
public class BallDetector implements Subsystem {

    private static final int BEAM_BREAK_ID = 9;
    private static final NTBoolean L_BALL_DETECTED = new NTBoolean("Hopper/Ball_Detector/Ball Detected", false);

    private final DigitalInput beamBreak;
    private boolean ballDetected;
    private boolean lastDetected;

    public BallDetector() {
        L_BALL_DETECTED.setTemporary();

        beamBreak = new DigitalInput(BEAM_BREAK_ID);
        ballDetected = false;
        lastDetected = false;
    }

    @Override
    public void periodic() {
        ballDetected = !beamBreak.get();

        L_BALL_DETECTED.set(ballDetected);
    }

    public boolean isBallDetected() {
        return ballDetected;
    }

    /**
     * Reads if the ball was there last tick and now is not
     * @return
     */
    public boolean isBallGone() {
        return !ballDetected && lastDetected;
    }

    @Override
    public String toString() {
        return String.valueOf(ballDetected);
    }
}
