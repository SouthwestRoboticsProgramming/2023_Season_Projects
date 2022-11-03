package com.swrobotics.robot.blockauto.part;

import com.team2129.lib.messenger.MessageBuilder;

public interface BlockPart {
    /**
     * Must write the corresponding type ID from {@link PartTypes},
     * then optionally any additional data.
     * 
     * @param builder builder to write to
     */
    void writeToMessenger(MessageBuilder builder);
}
