package com.swrobotics.robot.blockauto;

import java.util.ArrayList;
import java.util.List;

import com.team2129.lib.messenger.MessageBuilder;

public final class BlockCategory {
    private final String name;
    private final List<BlockDef> blocks;

    public BlockCategory(String name) {
        this.name = name;
        blocks = new ArrayList<>();
    }

    public BlockDef newBlock(String name) {
        BlockDef def = new BlockDef(name);
        blocks.add(def);
        return def;
    }

    public List<BlockDef> getBlocks() {
        return blocks;
    }

    public void writeToMessenger(MessageBuilder builder) {
        builder.addString(name);
        builder.addInt(blocks.size());
        for (BlockDef def : blocks) {
            def.writeToMessenger(builder);
        }
    }
}
