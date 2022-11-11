package com.swrobotics.lib.schedule;

import com.swrobotics.lib.time.Duration;
import com.swrobotics.lib.time.TimeUnit;
import com.swrobotics.lib.time.Timestamp;

/**
 * Command that simply waits for a specified time.
 */
public final class WaitCommand implements Command {
    private final Duration dur;
    private Timestamp end;

    /**
     * Creates a new instance with a specified time.
     * 
     * @param amt amount of time
     * @param unit time unit the amount is specified in
     */
    public WaitCommand(double amt, TimeUnit unit) {
        this(new Duration(amt, unit));
    }

    /**
     * Creates a new instance with a specified duration.
     * 
     * @param dur duration to run for
     */
    public WaitCommand(Duration dur) {
        this.dur = dur;
    }

    @Override
    public void init() {
        end = Timestamp.now().after(dur);
    }

    @Override
    public boolean run() {
        return Timestamp.now().isAtOrAfter(end);
    }
}
