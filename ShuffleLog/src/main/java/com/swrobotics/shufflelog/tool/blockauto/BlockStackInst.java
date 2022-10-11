package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.messenger.client.MessageReader;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public final class BlockStackInst {
    public static BlockStackInst read(MessageReader reader, BlockAutoTool tool) {
        int len = reader.readInt();
        BlockStackInst inst = new BlockStackInst();

        for (int i = 0; i < len; i++) {
            String name = reader.readString();
            BlockDef def = tool.getBlockDef(name);
            BlockInst block = def.readInstance(reader, tool);
            inst.blocks.add(block);
        }

        return inst;
    }

    private final List<BlockInst> blocks;

    public BlockStackInst() {
        blocks = new ArrayList<>();
    }

    public void show() {
        int i = 0;
        for (BlockInst block : blocks) {
            ImGui.pushID(i++);
            block.draw();
            ImGui.popID();
        }
    }
}
