package com.team2129.lib.time;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!o.getClass().equals(getClass())) return false;

        Duration dur = (Duration) o;
        return dur.count == count && dur.unit == unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, unit);
    }
}
