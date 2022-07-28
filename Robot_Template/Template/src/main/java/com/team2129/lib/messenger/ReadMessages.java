package com.team2129.lib.messenger;

import com.team2129.lib.schedule.subsystem.Subsystem;

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
