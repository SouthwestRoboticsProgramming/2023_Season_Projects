package com.swrobotics.pathfinding.lib;

import java.util.BitSet;

public final class BitfieldGrid extends Grid {
    private final BitSet data;

    public BitfieldGrid(int width, int height) {
        super(width, height);
        data = new BitSet(width * height);
        clear();
    }

    public void set(int x, int y, boolean value) {
        data.set(x + y * width, value);
    }

    public void copyFrom(BitfieldGrid other) {
        for (int x = 0; x < width && x < other.width; x++)
            for (int y = 0; y < height && y < other.height; y++)
                set(x, y, other.canCellPass(x, y));
    }

    @Override
    public boolean canCellPass(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= width)
            return true;
        return data.get(x + y * width);
    }

    public void clear() {
        data.set(0, data.size());
    }
}
