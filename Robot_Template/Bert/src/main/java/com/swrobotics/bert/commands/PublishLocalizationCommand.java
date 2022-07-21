package com.swrobotics.bert.commands;

import com.swrobotics.bert.subsystems.Localization;
import com.swrobotics.messenger.client.MessengerClient;

import static com.swrobotics.bert.constants.CommunicationConstants.*;

public final class PublishLocalizationCommand implements Command {
    private final MessengerClient msg;
    private final Localization loc;

    public PublishLocalizationCommand(MessengerClient msg, Localization loc) {
        this.msg = msg;
        this.loc = loc;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean run() {
        msg.builder(LOCALIZATION_MSG)
                .addDouble(loc.getFieldX())
                .addDouble(loc.getFieldY())
                .addDouble(-loc.getFieldRot().getDegrees())
                .send();

        return false;
    }

    @Override
    public void end() {

    }
}
