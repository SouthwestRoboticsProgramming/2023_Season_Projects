package com.swrobotics.robot.blockauto;

import java.util.ArrayList;
import java.util.List;

import com.team2129.lib.messenger.MessageReader;
import com.team2129.lib.schedule.Command;
import com.team2129.lib.schedule.CommandSequence;

public final class BlockStackInst {
    public static BlockStackInst read(MessageReader reader) {
        int len = reader.readInt();
        BlockStackInst inst = new BlockStackInst();

        for (int i = 0; i < len; i++) {
            String name = reader.readString();
            BlockDef def = AutoBlocks.getBlockDef(name);
            inst.blocks.add(def.readInstance(reader));
        }

        return inst;
    }

    private final List<BlockInst> blocks;

    public BlockStackInst() {
        blocks = new ArrayList<>();
    }

    public Command toCommand() {
        CommandSequence seq = new CommandSequence();
        for (BlockInst block : blocks) {
            seq.append(block.toCommand());
        }
        return seq;
    }
}
