package com.swrobotics.robot.blockauto.part;

import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.messenger.MessageReader;

public final class BooleanPart implements ParamPart {
    private final boolean def;

    public BooleanPart(boolean def) {
        this.def = def;
    }

    @Override
    public Object readInst(MessageReader reader) {
        return reader.readBoolean();
    }

    @Override
    public void writeInst(MessageBuilder builder, Object val) {
        builder.addBoolean((boolean) val);
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(PartTypes.BOOLEAN.getId());
        builder.addBoolean(def);
    }
}

