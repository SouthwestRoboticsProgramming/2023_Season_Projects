package com.swrobotics.shufflelog.tool.blockauto.part;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.tool.blockauto.BlockAutoTool;

public abstract class ParamPart extends BlockPart {
    public abstract Object getDefault();

    // Show ImGui editor tool
    public abstract Object edit(Object prev);

    public abstract Object readInst(MessageReader reader, BlockAutoTool tool);
}
