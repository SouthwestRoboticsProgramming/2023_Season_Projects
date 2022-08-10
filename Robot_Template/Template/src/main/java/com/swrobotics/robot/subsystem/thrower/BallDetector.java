package com.swrobotics.robot.subsystem.thrower;

import edu.wpi.first.wpilibj.DigitalInput;

import com.team2129.lib.schedule.Subsystem;

/**
 * A sensor to detect when a ball is engaged with the indexing wheel.
 */
public class BallDetector implements Subsystem {

    private static final int BEAM_BREAK_ID = 9;

    private final DigitalInput beamBreak;
    private boolean ballDetected;

    public BallDetector() {
        beamBreak = new DigitalInput(BEAM_BREAK_ID);
        ballDetected = false;
    }

    @Override
    public void periodic() {
        ballDetected = !beamBreak.get();

        // TOOD: Show ballDetected
    }

    public boolean isBallDetected() {
        return ballDetected;
    }

    @Override
    public String toString() {
        return String.valueOf(ballDetected);
    }
}
