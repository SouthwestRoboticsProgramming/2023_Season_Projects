package com.swrobotics.robot.blockauto.part;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;

public final class DoublePart implements ParamPart {
    private final double def;

    public DoublePart(double def) {
        this.def = def;
    }

    @Override
    public Object readInst(MessageReader reader) {
        return reader.readDouble();
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(PartTypes.DOUBLE.getId());
        builder.addDouble(def);
    }
}
