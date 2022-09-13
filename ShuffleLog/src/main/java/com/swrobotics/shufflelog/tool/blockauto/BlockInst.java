package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.shufflelog.tool.blockauto.part.BlockPart;
import com.swrobotics.shufflelog.tool.blockauto.part.ParamPart;
import com.swrobotics.shufflelog.tool.blockauto.part.StaticPart;
import imgui.ImGui;

public final class BlockInst {
    private final BlockDef def;
    private final Object[] params;

    public BlockInst(BlockDef def, Object[] params) {
        this.def = def;
        this.params = params;
    }

    public void draw() {
        int paramIdx = 0;
        int id = 0;
        boolean first = true;
        for (BlockPart part : def.getParts()) {
            if (first) {
                first = false;
            } else {
                ImGui.sameLine();
            }

            ImGui.pushID(id++);
            if (part instanceof ParamPart) {
                ParamPart p = (ParamPart) part;
                params[paramIdx] = p.edit(params[paramIdx]);
                paramIdx++;
            } else if (part instanceof StaticPart) {
                StaticPart s = (StaticPart) part;
                s.draw();
            }
            ImGui.popID();
        }
    }
}
