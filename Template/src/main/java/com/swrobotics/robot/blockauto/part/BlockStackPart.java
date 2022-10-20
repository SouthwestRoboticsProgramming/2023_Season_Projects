package com.swrobotics.robot.blockauto.part;

import com.swrobotics.robot.blockauto.BlockStackInst;
import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;

public final class BlockStackPart implements ParamPart {
    @Override
    public Object readInst(MessageReader reader) {
        return BlockStackInst.readFromMessenger(reader);
    }

    @Override
    public void writeInst(MessageBuilder builder, Object val) {
        ((BlockStackInst) val).write(builder);
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(PartTypes.BLOCK_STACK.getId());
    }
}
