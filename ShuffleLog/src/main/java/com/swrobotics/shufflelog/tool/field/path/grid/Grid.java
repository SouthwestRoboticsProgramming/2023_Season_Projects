package com.swrobotics.shufflelog.tool.field.path.grid;

import com.swrobotics.messenger.client.MessageReader;

import java.util.UUID;

public abstract class Grid {
    // Type IDs
    private static final byte UNION = 0;
    private static final byte BITFIELD = 1;
    private static final byte SHAPE = 2;

    private final UUID id;

    public Grid(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public abstract void readContent(MessageReader reader);

    public static Grid read(MessageReader reader) {
        long idMsb = reader.readLong();
        long idLsb = reader.readLong();
        UUID id = new UUID(idMsb, idLsb);

        byte type = reader.readByte();
        Grid grid;
        switch (type) {
            case UNION:
                grid = new GridUnion(id);
                break;
            case BITFIELD:
                grid = new BitfieldGrid(id);
                break;
            case SHAPE:
                grid = new ShapeGrid(id);
                break;
            default:
                throw new RuntimeException("Unknown type id: " + type);
        }
        grid.readContent(reader);

        return grid;
    }
}
