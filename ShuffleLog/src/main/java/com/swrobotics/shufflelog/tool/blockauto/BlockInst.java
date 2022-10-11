package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.shufflelog.tool.blockauto.part.BlockPart;
import com.swrobotics.shufflelog.tool.blockauto.part.ParamPart;
import com.swrobotics.shufflelog.tool.blockauto.part.StaticPart;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;

import java.util.List;

public final class BlockInst {
    private final BlockDef def;
    private final Object[] params;
    private final boolean firstElemRequiresAlignToFrame;

    public BlockInst(BlockDef def, Object[] params) {
        this.def = def;
        this.params = params;

        List<BlockPart> parts = def.getParts();
        if (parts.isEmpty() || parts.get(0).isFrame())
            firstElemRequiresAlignToFrame = false;
        else {
            boolean hasFrame = false;
            for (BlockPart part : parts) {
                if (part.isFrame()) {
                    hasFrame = true;
                    break;
                }
            }
            firstElemRequiresAlignToFrame = hasFrame;
        }
    }

    public BlockDef getDef() {
        return def;
    }

    private void drawContent() {
        int paramIdx = 0;
        int id = 0;
        boolean first = true;
        for (BlockPart part : def.getParts()) {
            if (first) {
                if (firstElemRequiresAlignToFrame)
                    ImGui.alignTextToFramePadding();
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

    public void draw() {
        ImGui.beginGroup();
        drawContent();
        ImGui.endGroup();
        float pad = ImGui.getStyle().getItemSpacingY() / 2;
        ImGui.getWindowDrawList().addRect(ImGui.getItemRectMinX()-pad, ImGui.getItemRectMinY()-pad, ImGui.getItemRectMaxX()+pad, ImGui.getItemRectMaxY()+pad, 0xFFFFFFFF);
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
            drawContent();
            ImGui.setDragDropPayload(BlockAutoTool.BLOCK_DND_ID, this);
            ImGui.endDragDropSource();
        }
    }
}
