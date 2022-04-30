package com.swrobotics.messenger.test;

import com.swrobotics.messenger.client.MessengerClient;

public final class MessengerTest {
    private static void sleepWithRead(MessengerClient msg) {
        for (int i = 0; i < 10; i++) {
            msg.readMessages();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    public static void main(String[] args) {
        MessengerClient msg = new MessengerClient("localhost", 5805, "Test");

        while (!msg.isConnected()) {
            Thread.onSpinWait();
        }

        msg.addHandler("Test", (reader) -> {
            System.out.println("Direct: " + reader.readInt());
        });
        msg.addHandler("Test2*", (reader) -> {
            System.out.println("Wildcard: " + reader.readInt());
        });

        for (int i = 0; i < 5; i++) {
            msg.prepare("Test")
                    .addInt(i)
                    .send();

            sleepWithRead(msg);
        }

        for (int i = 0; i < 5; i++) {
            msg.prepare("Test2" + Math.random())
                    .addInt(i)
                    .send();

            sleepWithRead(msg);
        }

        msg.disconnect();
    }
}
