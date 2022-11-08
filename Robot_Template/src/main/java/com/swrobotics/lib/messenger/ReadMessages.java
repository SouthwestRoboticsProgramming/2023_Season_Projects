package com.swrobotics.lib.messenger;

import com.swrobotics.lib.schedule.Subsystem;

public final class ReadMessages implements Subsystem {
    private final MessengerClient msg;

    public ReadMessages(MessengerClient msg) {
        this.msg = msg;
    }

    @Override
    public void periodic() {
        msg.readMessages();
    }
}
