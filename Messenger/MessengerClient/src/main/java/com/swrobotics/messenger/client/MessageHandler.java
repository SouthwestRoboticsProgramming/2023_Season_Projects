package com.swrobotics.messenger.client;

@FunctionalInterface
public interface MessageHandler {
    void handle(String type, MessageReader reader);
}
