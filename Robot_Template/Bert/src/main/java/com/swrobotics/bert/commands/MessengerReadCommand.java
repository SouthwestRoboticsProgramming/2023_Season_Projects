package com.swrobotics.bert.commands;

import com.swrobotics.messenger.client.MessengerClient;

public final class MessengerReadCommand implements Command {
    private final MessengerClient msg;

    public MessengerReadCommand(MessengerClient msg) {
        this.msg = msg;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean run() {
        msg.readMessages();

        return false;
    }

    @Override
    public void end() {

    }
}
