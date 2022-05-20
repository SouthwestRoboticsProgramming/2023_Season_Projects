package com.swrobotics.lib.math;

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

    public void setCWRad(double a) {
        angle = -a;
    }

    public void setCCWRad(double a) {
        angle = a;
    }

    public void setCWDeg(double a) {
        angle = Math.toRadians(-a);
    }

    public void setCCWDeg(double a) {
        angle = Math.toRadians(a);
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

    public Angle add(Angle o) {
        angle += o.angle;
        return this;
    }

    public Angle addCWRad(double a) {
        angle -= a;
        return this;
    }

    public Angle addCCWRad(double a) {
        angle += a;
        return this;
    }

    public Angle addCWDeg(double a) {
        angle -= Math.toRadians(a);
        return this;
    }

    public Angle addCCWDeg(double a) {
        angle += Math.toRadians(a);
        return this;
    }

    public Angle add(Angle o, Angle dest) {
        dest.angle = angle + o.angle;
        return dest;
    }

    public Angle addCWRad(double a, Angle dest) {
        dest.angle = angle - a;
        return dest;
    }

    public Angle addCCWRad(double a, Angle dest) {
        dest.angle = angle + a;
        return dest;
    }

    public Angle addCWDeg(double a, Angle dest) {
        dest.angle = angle - Math.toRadians(a);
        return dest;
    }

    public Angle addCCWDeg(double a, Angle dest) {
        dest.angle = angle + Math.toRadians(a);
        return dest;
    }

    public Angle sub(Angle o) {
        angle -= o.angle;
        return this;
    }

    public Angle subCWRad(double a) {
        angle += a;
        return this;
    }

    public Angle subCCWRad(double a) {
        angle -= a;
        return this;
    }

    public Angle subCWDeg(double a) {
        angle += Math.toRadians(a);
        return this;
    }

    public Angle subCCWDeg(double a) {
        angle -= Math.toRadians(a);
        return this;
    }

    public Angle sub(Angle o, Angle dest) {
        dest.angle = angle - o.angle;
        return dest;
    }

    public Angle subCWRad(double a, Angle dest) {
        dest.angle = angle + a;
        return dest;
    }

    public Angle subCCWRad(double a, Angle dest) {
        dest.angle = angle - a;
        return dest;
    }

    public Angle subCWDeg(double a, Angle dest) {
        dest.angle = angle + Math.toRadians(a);
        return dest;
    }

    public Angle subCCWDeg(double a, Angle dest) {
        dest.angle = angle - Math.toRadians(a);
        return dest;
    }

    @Override
    public String toString() {
        return "Angle{" +
                "ccwdeg=" + getCCWDeg() +
                '}';
    }
}
