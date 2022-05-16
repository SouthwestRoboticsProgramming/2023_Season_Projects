package com.swrobotics.mathlib;

import java.util.Objects;

public final class Vec2d {
    public double x;
    public double y;

    public Vec2d() {
        this(0, 0);
    }

    public Vec2d(double d) {
        this(d, d);
    }

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
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

    public Vec2d zero() {
        x = 0;
        y = 0;
        return this;
    }

    public Vec2d negateEq() {
        x = -x;
        y = -y;
        return this;
    }

    public Vec2d negate() {
        return new Vec2d(-x, -y);
    }

    public Vec2d absoluteEq() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public Vec2d absolute() {
        return new Vec2d(
                Math.abs(x),
                Math.abs(y)
        );
    }

    public Vec2d normalizeEq() {
        double len = length();
        x /= len;
        y /= len;
        return this;
    }

    public Vec2d normalize() {
        double len = length();
        return new Vec2d(
                x / len,
                y / len
        );
    }

    public Vec2d rotate(Angle angle) {
        double sin = angle.getSin();
        double cos = angle.getCos();

        return new Vec2d(
                cos * x - sin * y,
                sin * x + cos * y
        );
    }

    public Vec2d rotateEq(Angle angle) {
        double sin = angle.getSin();
        double cos = angle.getCos();

        double nx = cos * x - sin * y;
        double ny = sin * x + cos * y;
        x = nx;
        y = ny;

        return this;
    }

    public Vec2d rotateAround(double originX, double originY, Angle angle) {
        return sub(originX, originY)
                .rotateEq(angle)
                .addEq(originX, originY);
    }

    public Vec2d rotateAround(Vec2d origin, Angle angle) {
        return sub(origin)
                .rotateEq(angle)
                .addEq(origin);
    }

    public Vec2d rotateAroundEq(double originX, double originY, Angle angle) {
        return subEq(originX, originY)
                .rotateEq(angle)
                .addEq(originX, originY);
    }

    public Vec2d rotateAroundEq(Vec2d origin, Angle angle) {
        return subEq(origin)
                .rotateEq(angle)
                .addEq(origin);
    }

    public Angle angle() {
        return Angle.ccwRadians(Math.atan2(y, x));
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public double distance(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distance(Vec2d vec) {
        double dx = x - vec.x;
        double dy = y - vec.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceSquared(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return dx * dx + dy * dy;
    }

    public double distanceSquared(Vec2d vec) {
        double dx = x - vec.x;
        double dy = y - vec.y;
        return dx * dx + dy * dy;
    }

    public Vec2d addEq(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec2d add(double x, double y) {
        return new Vec2d(
                this.x + x,
                this.y + y
        );
    }

    public Vec2d addEq(double d) {
        x += d;
        y += d;
        return this;
    }

    public Vec2d add(double d) {
        return new Vec2d(
                x + d,
                y + d
        );
    }

    public Vec2d addEq(Vec2d vec) {
        x += vec.x;
        y += vec.y;
        return this;
    }

    public Vec2d add(Vec2d vec) {
        return new Vec2d(
                x + vec.x,
                y + vec.y
        );
    }

    public Vec2d subEq(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vec2d sub(double x, double y) {
        return new Vec2d(
                this.x - x,
                this.y - y
        );
    }

    public Vec2d subEq(double d) {
        x -= d;
        y -= d;
        return this;
    }

    public Vec2d sub(double d) {
        return new Vec2d(
                x - d,
                y - d
        );
    }

    public Vec2d subEq(Vec2d vec) {
        x -= vec.x;
        y -= vec.y;
        return this;
    }

    public Vec2d sub(Vec2d vec) {
        return new Vec2d(
                x - vec.x,
                y - vec.y
        );
    }

    public Vec2d mulEq(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vec2d mul(double x, double y) {
        return new Vec2d(
                this.x * x,
                this.y * y
        );
    }

    public Vec2d mulEq(double d) {
        x *= d;
        y *= d;
        return this;
    }

    public Vec2d mul(double d) {
        return new Vec2d(
                x * d,
                y * d
        );
    }

    public Vec2d mulEq(Vec2d vec) {
        x *= vec.x;
        y *= vec.y;
        return this;
    }

    public Vec2d mul(Vec2d vec) {
        return new Vec2d(
                x * vec.x,
                y * vec.y
        );
    }

    public Vec2d divEq(double x, double y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vec2d div(double x, double y) {
        return new Vec2d(
                this.x / x,
                this.y / y
        );
    }

    public Vec2d divEq(double d) {
        x /= d;
        y /= d;
        return this;
    }

    public Vec2d div(double d) {
        return new Vec2d(
                x / d,
                y / d
        );
    }

    public Vec2d divEq(Vec2d vec) {
        x /= vec.x;
        y /= vec.y;
        return this;
    }

    public Vec2d div(Vec2d vec) {
        return new Vec2d(
                x / vec.x,
                y / vec.y
        );
    }

    public boolean equals(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2d vec2d = (Vec2d) o;
        return Double.compare(vec2d.x, x) == 0 && Double.compare(vec2d.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
