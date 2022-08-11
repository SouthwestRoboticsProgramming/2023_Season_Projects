package com.swrobotics.pathfinding.grid;

public final class GridTypeIds {
    public static final byte UNION = 0;
    public static final byte BITFIELD = 1;
    public static final byte SHAPE = 2;

    private GridTypeIds() {
        throw new AssertionError();
    }
}
