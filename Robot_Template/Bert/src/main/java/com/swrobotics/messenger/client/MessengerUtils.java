package com.swrobotics.messenger.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class MessengerUtils {
    public static byte[] encodeStringUTF(String str) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b.toByteArray();
    }

    private MessengerUtils() {
        throw new AssertionError();
    }
}
