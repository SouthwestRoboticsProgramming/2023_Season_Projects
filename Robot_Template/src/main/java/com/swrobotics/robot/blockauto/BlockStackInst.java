package com.swrobotics.robot.blockauto;

import java.util.ArrayList;
import java.util.List;

import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.messenger.MessageReader;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.lib.schedule.CommandSequence;
import com.swrobotics.robot.Robot;

public final class BlockStackInst {
    public static BlockStackInst readFromMessenger(MessageReader reader) {
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

    public void addBlock(BlockInst block) {
        blocks.add(block);
    }
    
    public void write(MessageBuilder builder) {
        builder.addInt(blocks.size());
        for (BlockInst block : blocks) {
            block.write(builder);
        }
    }

    public Command toCommand(Robot robot) {
        CommandSequence seq = new CommandSequence();
        for (BlockInst block : blocks) {
            seq.append(block.toCommand(robot));
        }
        return seq;
    }
}
