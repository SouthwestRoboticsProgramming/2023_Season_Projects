package com.team2129.lib.schedule;

import com.team2129.lib.time.Duration;
import com.team2129.lib.time.TimeUnit;
import com.team2129.lib.time.Timestamp;

// FIXME: Docs
public final class WaitCommand implements Command {
    private final Duration dur;
    private Timestamp end;
    private boolean initiated;

    public WaitCommand(double amt, TimeUnit unit) {
        this(new Duration(amt, unit));
    }

    public WaitCommand(Duration dur) {
        initiated = false;
        this.dur = dur;
    }

    @Override
    public boolean run() {
        // Start the wait when the command starts
        if (!initiated) {
            end = Timestamp.now().after(dur);
        }
        System.out.println("Hi");
        initiated = true;
        return Timestamp.now().isAtOrAfter(end);
    }
}
