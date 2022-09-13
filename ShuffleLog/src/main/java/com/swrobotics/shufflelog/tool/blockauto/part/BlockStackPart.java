package com.swrobotics.shufflelog.tool.blockauto.part;

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
}
