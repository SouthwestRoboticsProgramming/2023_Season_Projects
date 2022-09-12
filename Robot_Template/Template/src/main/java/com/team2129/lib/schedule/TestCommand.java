package com.team2129.lib.schedule;

import edu.wpi.first.wpilibj.Timer;

public abstract class TestCommand implements Command {

    private final Timer timeoutTimer = new Timer();
    private double timeoutSeconds = 2.0;

    protected void setTimeout(double timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    protected double getTime() {
        return timeoutTimer.get();
    }

    protected boolean hasTimedOut() {
        timeoutTimer.start(); // TODO: How can I start the timer right away?
        return timeoutTimer.hasElapsed(timeoutSeconds);
    }
    
    // public TestCommand(double timeoutSeconds) {
    //     this.timeoutSeconds = timeoutSeconds;
    //     timeoutTimer = new Timer();
    // }

    /**
     * Returns if the test condition was not met
     * @return If the test condition has not been met.
     */
    public abstract boolean hasFailed();
}
