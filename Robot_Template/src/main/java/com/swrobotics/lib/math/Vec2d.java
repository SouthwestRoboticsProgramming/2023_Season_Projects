package com.swrobotics.lib.math;

import java.util.Objects;

import edu.wpi.first.math.geometry.Translation2d;

// TODO: Docs
public final class Vec2d {
    public double x;
    public double y;

    public Vec2d() {
        x = 0;
        y = 0;
    }

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d(Angle angle, double magnitude) {
        this.y = angle.getSin() * magnitude;
        this.x = angle.getCos() * magnitude;
    }

    public Vec2d(Translation2d tx) {
        x = tx.getX();
        y = tx.getY();
    }

    public Vec2d(Vec2d v) {
        x = v.x;
        y = v.y;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public double magnitudeSquared() {
        return x * x + y * y;
    }

    public Angle angle() {
        return Angle.ccwRad(Math.atan2(y, x));
    }

    public Angle angleTo(Vec2d vec) {
        return Angle.ccwRad(Math.atan2(vec.y - y, vec.x - x));
    }

    public double distanceTo(double x, double y) {
        double deltaX = this.x - x;
        double deltaY = this.y - y;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public double distanceTo(Vec2d vec) {
        double deltaX = x - vec.x;
        double deltaY = y - vec.y;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public double distanceSquaredTo(double x, double y) {
        double deltaX = this.x - x;
        double deltaY = this.y - y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public double distanceSquaredTo(Vec2d vec) {
        double deltaX = x - vec.x;
        double deltaY = y - vec.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public Vec2d rotateBy(Angle angle) {
        double sin = angle.getSin();
        double cos = angle.getCos();

        double nx = x * cos - y * sin;
        double ny = x * sin + y * cos;

        x = nx;
        y = ny;
        return this;
    }

    public Vec2d rotateBy(Angle angle, Vec2d dest) {
        double sin = angle.getSin();
        double cos = angle.getCos();

        dest.x = x * cos - y * sin;
        dest.y = x * sin + y * cos;

        return dest;
    }

    public Vec2d add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec2d add(double x, double y, Vec2d dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        return dest;
    }

    public Vec2d add(Vec2d vec) {
        x += vec.x;
        y += vec.y;
        return this;
    }

    public Vec2d add(Vec2d vec, Vec2d dest) {
        dest.x = x + vec.x;
        dest.y = y + vec.y;
        return dest;
    }

    public Vec2d sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vec2d sub(double x, double y, Vec2d dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        return dest;
    }

    public Vec2d sub(Vec2d vec) {
        x -= vec.x;
        y -= vec.y;
        return this;
    }

    public Vec2d sub(Vec2d vec, Vec2d dest) {
        dest.x = x + vec.x;
        dest.y = y + vec.y;
        return dest;
    }

    public Vec2d mul(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vec2d mul(double scalar, Vec2d dest) {
        dest.x = x * scalar;
        dest.y = y * scalar;
        return dest;
    }

    public Vec2d mul(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vec2d mul(double x, double y, Vec2d dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        return dest;
    }

    public Vec2d mul(Vec2d vec) {
        x *= vec.x;
        y *= vec.y;
        return this;
    }

    public Vec2d mul(Vec2d vec, Vec2d dest) {
        dest.x = x * vec.x;
        dest.y = y * vec.y;
        return dest;
    }

    public Vec2d div(double scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    public Vec2d div(double scalar, Vec2d dest) {
        dest.x = x / scalar;
        dest.y = y / scalar;
        return dest;
    }

    public Vec2d div(double x, double y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vec2d div(double x, double y, Vec2d dest) {
        dest.x = this.x / x;
        dest.y = this.y / y;
        return dest;
    }

    public Vec2d div(Vec2d vec) {
        x /= vec.x;
        y /= vec.y;
        return this;
    }

    public Vec2d div(Vec2d vec, Vec2d dest) {
        dest.x = x / vec.x;
        dest.y = y / vec.y;
        return dest;
    }

    public Vec2d normalize() {
        double mag = magnitude();
        x /= mag;
        y /= mag;
        return this;
    }

    public Vec2d normalize(Vec2d dest) {
        double mag = magnitude();
        dest.x = x / mag;
        dest.y = y / mag;
        return dest;
    }

    public Vec2d negate() {
        x = -x;
        y = -y;
        return this;
    }

    public Vec2d negate(Vec2d dest) {
        dest.x = -x;
        dest.y = -y;
        return this;
    }

    public Vec2d absolute() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public Vec2d absolute(Vec2d dest) {
        dest.x = Math.abs(x);
        dest.y = Math.abs(y);
        return this;
    }

    public Vec2d set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2d set(Vec2d vec) {
        x = vec.x;
        y = vec.y;
        return this;
    }

    public Vec2d set(Translation2d tx) {
        x = tx.getX();
        y = tx.getY();
        return this;
    }

    public Translation2d toTranslation2d() {
        return new Translation2d(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2d vec2D = (Vec2d) o;
        return Double.compare(vec2D.x, x) == 0 &&
                Double.compare(vec2D.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Vec2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
