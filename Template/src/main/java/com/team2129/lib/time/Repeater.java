package com.team2129.lib.time;

public final class Repeater {
    private final Duration interval;
    private final Runnable tickFn;
    private Timestamp lastTickTime;

    public Repeater(Duration interval, Runnable tickFn) {
        this.interval = interval;
        this.tickFn = tickFn;
        lastTickTime = null;
    }

    public void tick() {
        if (lastTickTime == null)
            lastTickTime = Timestamp.now();

        Timestamp now = Timestamp.now();
        double nanoDiff = now.difference(lastTickTime).getDurationNanos();
        double nanoInt = interval.getDurationNanos();

        while (nanoDiff > nanoInt) {
            tickFn.run();
            lastTickTime = lastTickTime.addNanos(nanoInt);
            nanoDiff -= nanoInt;
        }
    }
}
