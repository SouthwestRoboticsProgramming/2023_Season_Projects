package com.swrobotics.shufflelog.tool.blockauto.part;

public abstract class ParamPart extends BlockPart {
    public abstract Object getDefault();

    // Show ImGui editor tool
    public abstract Object edit(Object prev);
}
