package com.swrobotics.mathlib;

public final class Angle {
    private double angle;

    public static Angle cwRadians(double cwRad) {
        return new Angle(-cwRad);
    }

    public static Angle ccwRadians(double ccwRad) {
        return new Angle(ccwRad);
    }

    public static Angle cwDegrees(double cwDeg) {
        return new Angle(Math.toRadians(-cwDeg));
    }

    public static Angle ccwDegrees(double ccwDeg) {
        return new Angle(Math.toRadians(ccwDeg));
    }

    private Angle(double angle) {
        this.angle = angle;
    }

    public double getCWRadians() {
        return -angle;
    }

    public double getCCWRadians() {
        return angle;
    }

    public double getCWDegrees() {
        return Math.toDegrees(-angle);
    }

    public double getCCWDegrees() {
        return Math.toDegrees(angle);
    }

    public double getSin() {
        return Math.sin(angle);
    }

    public double getCos() {
        return Math.cos(angle);
    }

    public Angle addEq(Angle a) {
        angle += a.angle;
        return this;
    }

    public Angle add(Angle a) {
        return new Angle(angle + a.angle);
    }

    public Angle subEq(Angle a) {
        angle -= a.angle;
        return this;
    }

    public Angle sub(Angle a) {
        return new Angle(angle - a.angle);
    }

    public Angle mulEq(Angle a) {
        angle *= a.angle;
        return this;
    }

    public Angle mul(Angle a) {
        return new Angle(angle * a.angle);
    }

    public Angle mulEq(double scalar) {
        angle *= scalar;
        return this;
    }

    public Angle mul(double scalar) {
        return new Angle(angle * scalar);
    }

    public Angle divEq(Angle a) {
        angle /= a.angle;
        return this;
    }

    public Angle div(Angle a) {
        return new Angle(angle / a.angle);
    }

    public Angle divEq(double scalar) {
        angle /= scalar;
        return this;
    }

    public Angle div(double scalar) {
        return new Angle(angle / scalar);
    }

    public enum StringFormat {
        CW_RADIANS {
            @Override
            public String get(Angle a) {
                return a.getCWRadians() + " rad cw";
            }
        },
        CCW_RADIANS {
            @Override
            public String get(Angle a) {
                return a.getCCWRadians() + " rad ccw";
            }
        },
        CW_DEGREES {
            @Override
            public String get(Angle a) {
                return a.getCWDegrees() + " deg cw";
            }
        },
        CCW_DEGREES {
            @Override
            public String get(Angle a) {
                return a.getCCWDegrees() + " deg ccw";
            }
        };

        protected abstract String get(Angle a);
    }

    @Override
    public String toString() {
        return toString(StringFormat.CCW_DEGREES);
    }

    public String toString(StringFormat format) {
        return format.get(this);
    }
}
