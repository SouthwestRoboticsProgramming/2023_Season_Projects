package com.swrobotics.lib.messenger;

@FunctionalInterface
public interface MessageHandler {
    void handle(String type, MessageReader reader);
}
