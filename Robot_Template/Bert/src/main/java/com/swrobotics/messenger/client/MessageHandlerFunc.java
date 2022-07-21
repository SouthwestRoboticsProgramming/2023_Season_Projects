package com.swrobotics.messenger.client;

import java.io.DataInputStream;
import java.io.IOException;

@FunctionalInterface
public interface MessageHandlerFunc {
    void handle(String type, DataInputStream in) throws IOException;
}
