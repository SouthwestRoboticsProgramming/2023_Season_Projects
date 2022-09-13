package com.team2129.lib.schedule;

import com.team2129.lib.time.Duration;
import com.team2129.lib.time.TimeUnit;
import com.team2129.lib.time.Timestamp;

public final class WaitCommand implements Command {
    private final Timestamp end;

    public WaitCommand(double amt, TimeUnit unit) {
        this(new Duration(amt, unit));
    }

    public WaitCommand(Duration dur) {
        end = Timestamp.now().after(dur);
    }

    @Override
    public boolean run() {
        return Timestamp.now().isAtOrAfter(end);
    }
}
