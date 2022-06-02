package com.swrobotics.lib.math;

import org.opencv.core.RotatedRect;

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

    public Rotation2d toRotation2dCW() {
        return new Rotation2d(getCWRad());
    }

    public Rotation2d toRotation2dCCW() {
        return new Rotation2d(getCCWRad());
    }

    // Compare Angles
    public boolean greaterThan(Angle comparison) {
        return angle > comparison.angle;
    }

    public boolean lessThan(Angle comparison) {
        return angle < comparison.angle;
    }

    public boolean greaterOrEqualTo(Angle comparison) {
        return angle >= comparison.angle;
    }

    public boolean lessOrEqualTo(Angle comparison) {
        return angle <= comparison.angle;
    }


    @Override
    public String toString() {
        return "Angle{" +
                "ccwdeg=" + getCCWDeg() +
                '}';
    }
}
