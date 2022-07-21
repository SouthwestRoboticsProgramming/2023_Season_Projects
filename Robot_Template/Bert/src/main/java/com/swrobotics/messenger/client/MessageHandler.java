package com.swrobotics.messenger.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public final class MessageHandler {
    private final MessengerClient client;
    private MessageHandlerFunc handler;

    public MessageHandler(MessengerClient client) {
        this.client = client;
        handler = (t, d) -> {};
    }

    public MessageHandler listen(String type) {
        client.addListener(type, this);
        return this;
    }

    public MessageHandler unlisten(String type) {
        client.removeListener(type, this);
        return this;
    }

    public MessageHandler setHandler(MessageHandlerFunc handler) {
        this.handler = handler;
        return this;
    }

    public void remove() {
        client.removeListenerFully(this);
    }

    void dispatch(String type, byte[] data) {
        try {
            handler.handle(type, new DataInputStream(new ByteArrayInputStream(data)));
        } catch (IOException e) {
            System.err.println("Exception in message handler:");
            e.printStackTrace();
        }
    }
}
