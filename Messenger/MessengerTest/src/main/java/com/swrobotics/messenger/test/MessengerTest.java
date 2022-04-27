package com.swrobotics.messenger.test;

import com.swrobotics.messenger.client.MessengerClient;

public final class MessengerTest {
    public static void main(String[] args) {
        MessengerClient msg = new MessengerClient("localhost", 5805, "Test");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
