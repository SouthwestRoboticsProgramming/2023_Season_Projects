package com.team2129.lib.messenger;

import com.team2129.lib.routine.Routine;

public final class MessengerReadRoutine extends Routine {
    private final MessengerClient msg;

    public MessengerReadRoutine(MessengerClient msg) {
        this.msg = msg;
    }

    @Override
    protected void periodic() {
        msg.readMessages();
    }
}
