package com.swrobotics.lidar.lib;

public final class LidarScanEntry {
    private final double distance;
    private final double angle;

    public LidarScanEntry(double distance, double angle) {
        this.distance = distance;
        this.angle = angle;
    }

    public double getDistance() {
        return distance;
    }

    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return "LidarScanEntry{" +
                "distance=" + distance +
                ", angle=" + angle +
                '}';
    }
}
