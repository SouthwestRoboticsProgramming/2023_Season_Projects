package com.swrobotics.robot.blockauto.part;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;

public interface ParamPart extends BlockPart {
    Object readInst(MessageReader reader);
    void writeInst(MessageBuilder builder, Object val);
}
