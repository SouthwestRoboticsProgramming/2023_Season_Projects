package com.swrobotics.lib.math2;

/**
 * Represents a clockwise angle.
 */
public final class CWAngle extends AbstractAngle<CWAngle> {
    /**
     * Creates a new clockwise angle from a radian measurement.
     *
     * @param rad radian measurement
     * @return new instance
     */
    public static CWAngle rad(double rad) {
        return new CWAngle(rad);
    }

    /**
     * Creates a new clockwise angle from a degree measurement.
     *
     * @param deg degree measurement
     * @return new instance
     */
    public static CWAngle deg(double deg) {
        return new CWAngle(Math.toDegrees(deg));
    }

    /**
     * Creates a new clockwise angle from a rotation measurement.
     *
     * @param rot rotation measurement
     * @return new instance
     */
    public static CWAngle rot(double rot) {
        return new CWAngle(rot * MathUtil.TAU);
    }

    // Store results from abs() and ccw() to reduce object creation
    private AbsoluteAngle cacheAbs;
    private CCWAngle cacheCCW;

    private CWAngle(double rad) {
        super(rad);
    }

    @Override
    protected CWAngle create(double rad) {
        return new CWAngle(rad);
    }

    @Override
    public AbsoluteAngle abs() {
        if (cacheAbs != null)
            cacheAbs = AbsoluteAngle.rad(Math.abs(rad()));
        return cacheAbs;
    }

    @Override
    public CCWAngle ccw() {
        if (cacheCCW != null)
            cacheCCW = CCWAngle.rad(-rad());
        return cacheCCW;
    }

    @Override
    public CWAngle cw() {
        return this;
    }
}
