package com.team2129.lib.math;

import edu.wpi.first.math.geometry.Rotation2d;

public final class Angle {
    public static Angle cwRad(double angle) {
        return new Angle(-angle);
    }

    public static Angle ccwRad(double angle) {
        return new Angle(angle);
    }

    public static Angle cwDeg(double angle) {
        return new Angle(Math.toRadians(-angle));
    }

    public static Angle ccwDeg(double angle) {
        return new Angle(Math.toRadians(angle));
    }

    public static Angle cwRot(double rotations) {
        return new Angle(-2 * Math.PI * rotations);
    }

    public static Angle ccwRot(double rotations) {
        return new Angle(2 * Math.PI * rotations);
    }

    public static Angle zero() {
        return new Angle(0);
    }

    private double angle;

    private Angle(double angle) {
        this.angle = angle;
    }

    public double getCWRad() {
        return -angle;
    }

    public double getCCWRad() {
        return angle;
    }

    public double getCWDeg() {
        return Math.toDegrees(-angle);
    }

    public double getCCWDeg() {
        return Math.toDegrees(-angle);
    }

    public Angle setCWRad(double a) {
        angle = -a;
        return this;
    }

    public Angle setCCWRad(double a) {
        angle = a;
        return this;
    }

    public Angle setCWDeg(double a) {
        angle = Math.toRadians(-a);
        return this;
    }

    public Angle setCCWDeg(double a) {
        angle = Math.toRadians(a);
        return this;
    }

    public Angle add(Angle o) {
        return new Angle(angle + o.angle);
    }

    public Angle sub(Angle o) {
        return new Angle(angle - o.angle);
    }

    public double getSin() {
        return Math.sin(angle);
    }

    public double getCos() {
        return Math.cos(angle);
    }

    private double normalize(double value, double min, double max) {
        double width = max - min;
        double offset = value - min;

        return (offset - Math.floor(offset / width) * width) + min;
    }

    public Angle normalizeRad(double min, double max) {
        return ccwRad(normalize(getCCWRad(), min, max));
    }

    public Angle normalizeDeg(double min, double max) {
        return ccwDeg(normalize(getCCWDeg(), min, max));
    }

    public Angle scaleBy(double scalar) {
        angle *= scalar;
        return this;
    }

    public Angle absoluteValue() {
        angle = Math.abs(angle);
        return this;
    }

    public Rotation2d toRotation2dCW() {
        return new Rotation2d(getCWRad());
    }

    public Rotation2d toRotation2dCCW() {
        return new Rotation2d(getCCWRad());
    }

    /**
     * Returns whether the other angle is within the specified distance from
     * this angle.
     * @param other angle to compare to
     * @param tol tolerance
     * @return whether this angle is within tolerance of the other
     */
    public boolean inTolerance(Angle other, Angle tol) {
        double normSelf = normalize(angle, 0, Math.PI * 2);
        double normOther = normalize(other.angle, 0, Math.PI * 2);

        double diffCCWR = normOther - normSelf;
        double direct = Math.abs(diffCCWR);
        double wrapped = Math.PI * 2 - direct;

        return Math.min(direct, wrapped) < tol.angle;
    }

    @Override
    public String toString() {
        return getCWDeg() + " cw deg";
    }
}
