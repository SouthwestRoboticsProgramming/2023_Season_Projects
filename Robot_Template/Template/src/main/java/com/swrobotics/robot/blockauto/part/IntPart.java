package com.swrobotics.robot.blockauto.part;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;

public final class IntPart implements ParamPart {
    private final int def;

    public IntPart(int def) {
        this.def = def;
    }

    @Override
    public Object readInst(MessageReader reader) {
        return reader.readInt();
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(PartTypes.INT.getId());
        builder.addInt(def);
    }
}
