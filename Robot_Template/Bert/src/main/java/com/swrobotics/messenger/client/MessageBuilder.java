package com.swrobotics.messenger.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class MessageBuilder {
    private final MessengerClient msg;
    private final String type;
    private final ByteArrayOutputStream bytesOut;
    private final DataOutputStream out;

    public MessageBuilder(MessengerClient msg, String type) {
        this.msg = msg;
        this.type = type;

        bytesOut = new ByteArrayOutputStream();
        out = new DataOutputStream(bytesOut);
    }

    public MessageBuilder addBoolean(boolean value) {
        try {
            out.writeBoolean(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageBuilder addInt(int value) {
        try {
            out.writeInt(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageBuilder addLong(long value) {
        try {
            out.writeLong(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageBuilder addDouble(double value) {
        try {
            out.writeDouble(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void send() {
        msg.sendMessage(type, bytesOut.toByteArray());
    }
}
