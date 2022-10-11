package com.swrobotics.shufflelog.tool.blockauto.part;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.tool.blockauto.BlockAutoTool;
import com.swrobotics.shufflelog.tool.blockauto.BlockStackInst;
import imgui.ImGui;

// TODO
public final class BlockStackPart extends ParamPart {
    @Override
    public Object getDefault() {
        return null;
    }

    @Override
    public Object edit(Object prev) {
        ImGui.text("<BlockStack>");
        return null;
    }

    @Override
    public Object readInst(MessageReader reader, BlockAutoTool tool) {
        return BlockStackInst.read(reader, tool);
    }
}
