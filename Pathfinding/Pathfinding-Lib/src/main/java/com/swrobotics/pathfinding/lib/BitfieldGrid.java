package com.swrobotics.pathfinding.lib;

import java.util.BitSet;

public final class BitfieldGrid extends Grid {
    private final BitSet data;

    public BitfieldGrid(int width, int height) {
        super(width, height);
        data = new BitSet(width * height);

        // Make all cells passable
        data.set(0, data.size());
    }

    private void set(int x, int y, boolean value) {
        data.set(x + y * width, value);
    }

    @Override
    public boolean canPass(int x, int y) {
        return data.get(x + y * width);
    }
}
