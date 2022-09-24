package com.team2129.lib.utils;

import com.team2129.lib.math.Vec2d;
import edu.wpi.first.math.geometry.Translation2d;

public final class CoordinateConversions {
    public static Vec2d fromWPICoords(Translation2d tx) {
        return new Vec2d(-tx.getY(), tx.getX());
    }

    public static Translation2d toWPICoords(Vec2d vec) {
        return new Translation2d(vec.y, -vec.x);
    }

    private CoordinateConversions() {
        throw new AssertionError();
    }
}
