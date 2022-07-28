package com.team2129.lib.time;

public final class Duration {
    private final double count;
    private final TimeUnit unit;

    public Duration(double count, TimeUnit unit) {
        this.count = count;
        this.unit = unit;
    }

    public double getCount() {
        return count;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public double getDurationNanos() {
        return count * unit.getDurationNanos();
    }
}
