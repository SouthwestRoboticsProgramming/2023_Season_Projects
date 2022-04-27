package com.swrobotics.messenger.client;

import java.io.DataInputStream;
import java.io.IOException;

public final class MessageReader {
    private final DataInputStream in;
    private boolean closed;

    public MessageReader(DataInputStream in) {
        this.in = in;
        closed = false;
    }

    public boolean readBoolean() {
        try {
            return in.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read boolean", e);
        }
    }

    public String readString() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read String", e);
        }
    }

    public char readChar() {
        try {
            return in.readChar();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read char", e);
        }
    }

    public byte readByte() {
        try {
            return in.readByte();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read byte", e);
        }
    }

    public short readShort() {
        try {
            return in.readShort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read short", e);
        }
    }

    public int readInt() {
        try {
            return in.readInt();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read int", e);
        }
    }

    public long readLong() {
        try {
            return in.readLong();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read long", e);
        }
    }

    public float readFloat() {
        try {
            return in.readFloat();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read float", e);
        }
    }

    public double readDouble() {
        try {
            return in.readDouble();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read double", e);
        }
    }

    public void close() {
        if (!closed) {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close reader", e);
            }

            closed = true;
        }
    }
}
