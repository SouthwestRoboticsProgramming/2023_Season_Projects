package com.team2129.lib.time;

public final class Timestamp {
    private final double nanoTime;

    public static Timestamp now() {
        return new Timestamp(System.nanoTime());
    }

    public Timestamp(double nanoTime) {
        this.nanoTime = nanoTime;
    }

    public Duration difference(Timestamp prevTime) {
        return new Duration(nanoTime - prevTime.nanoTime, TimeUnit.NANOSECONDS);
    }

    public Timestamp addNanos(double nanos) {
        return new Timestamp(nanoTime + nanos);
    }
}
