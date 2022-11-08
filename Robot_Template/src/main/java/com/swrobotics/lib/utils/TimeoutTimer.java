package com.swrobotics.lib.utils;

import edu.wpi.first.wpilibj.Timer;

public class TimeoutTimer {

    private final Timer timer;
    private final double timeoutSeconds;
    
    public TimeoutTimer(double timeoutSeconds) {
        timer = new Timer();
        this.timeoutSeconds = timeoutSeconds;
    }

    public void start(boolean reset) {
        if (reset) timer.reset();
        timer.start();
    }

    public void reset() {
        timer.reset();
    }

    public void stop() {
        timer.stop();
        timer.reset();
    }

    public double getTime() {
        return timer.get();
    }

    public boolean get() {
        return timer.hasElapsed(timeoutSeconds);
    }

    public void setPaused(boolean paused) {
        if (paused) {
            timer.stop();
        } else {
            timer.start();
        }
    }
}
