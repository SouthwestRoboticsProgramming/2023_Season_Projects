package com.swrobotics.lib.messenger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class MessageBuilder {
    private final MessengerClient client;
    private final String type;
    private final ByteArrayOutputStream b;
    private final DataOutputStream out;

    public MessageBuilder(MessengerClient client, String type) {
        this.client = client;
        this.type = type;
        b = new ByteArrayOutputStream();
        out = new DataOutputStream(b);
    }

    public void send() {
        client.sendMessage(type, b.toByteArray());
    }

    public MessageBuilder addBoolean(boolean b) {
        try {
            out.writeBoolean(b);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write boolean", e);
        }
        return this;
    }

    public MessageBuilder addString(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write String", e);
        }
        return this;
    }

    public MessageBuilder addChar(char c) {
        try {
            out.writeChar(c);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write char", e);
        }
        return this;
    }

    public MessageBuilder addByte(byte b) {
        try {
            out.writeByte(b);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write byte", e);
        }
        return this;
    }

    public MessageBuilder addShort(short s) {
        try {
            out.writeShort(s);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write short", e);
        }
        return this;
    }

    public MessageBuilder addInt(int i) {
        try {
            out.writeInt(i);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write int", e);
        }
        return this;
    }

    public MessageBuilder addLong(long l) {
        try {
            out.writeLong(l);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write long", e);
        }
        return this;
    }

    public MessageBuilder addFloat(float f) {
        try {
            out.writeFloat(f);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write float", e);
        }
        return this;
    }

    public MessageBuilder addDouble(double d) {
        try {
            out.writeDouble(d);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write double", e);
        }
        return this;
    }
}
