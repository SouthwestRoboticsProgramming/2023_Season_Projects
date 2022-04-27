package com.swrobotics.messenger.client;

@FunctionalInterface
public interface MessageHandler {
    void handle(MessageReader reader);
}
