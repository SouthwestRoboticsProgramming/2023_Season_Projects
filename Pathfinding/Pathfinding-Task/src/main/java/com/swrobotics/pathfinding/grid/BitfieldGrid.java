package com.swrobotics.pathfinding.grid;

import com.swrobotics.messenger.client.MessageBuilder;

import java.util.BitSet;

public class BitfieldGrid extends Grid {
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
        if (x < 0 || y < 0 || x >= width || y >= height)
            return false;
        return data.get(x + y * width);
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(GridTypeIds.BITFIELD);
        writeToMessengerNoTypeId(builder);
    }

    public void writeToMessengerNoTypeId(MessageBuilder builder) {
        builder.addInt(width);
        builder.addInt(height);
        long[] data = this.data.toLongArray();
        builder.addInt(data.length);
        for (long val : data) {
            builder.addLong(val);
        }
    }

    public void clear() {
        data.set(0, data.size());
    }
}
