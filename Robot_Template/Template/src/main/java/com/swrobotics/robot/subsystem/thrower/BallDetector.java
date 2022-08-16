package com.swrobotics.robot.subsystem.thrower;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.DigitalInput;

import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

/**
 * A sensor to detect when a ball is engaged with the indexing wheel.
 */
public class BallDetector implements Subsystem {

    private static final int BEAM_BREAK_ID = 9;
    private static final NTDouble DEBOUNCE_TIME = new NTDouble("Hopper/Balldetector/Debounce_Time", 1/ 50 * 2); // 2 Ticks (Prevent a flick)

    private final DigitalInput beamBreak;
    private final Debouncer debouncer;
    private boolean ballDetected;
    private boolean lastDetected;

    public BallDetector() {
        beamBreak = new DigitalInput(BEAM_BREAK_ID);
        debouncer = new Debouncer(DEBOUNCE_TIME.get());
        ballDetected = false;
        lastDetected = false;
    }

    @Override
    public void periodic() {
        ballDetected = debouncer.calculate(!beamBreak.get());

        // TOOD: Show ballDetected
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
