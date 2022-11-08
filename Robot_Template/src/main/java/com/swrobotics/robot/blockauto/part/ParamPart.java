package com.swrobotics.robot.blockauto.part;

import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.messenger.MessageReader;

public interface ParamPart extends BlockPart {
    Object readInst(MessageReader reader);
    void writeInst(MessageBuilder builder, Object val);
}
