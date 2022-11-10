package com.swrobotics.robot.blockauto;

import java.util.function.Function;

import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.robot.Robot;
import com.swrobotics.robot.blockauto.part.BlockPart;
import com.swrobotics.robot.blockauto.part.ParamPart;

/**
 * An instance of a {@link BlockDef}.
 */
public final class BlockInst {
    private final BlockDef def;
    private final Object[] params;

    public BlockInst(BlockDef def, Object... params) {
        this.def = def;
        this.params = params;
    }

    public void write(MessageBuilder builder1) {
        builder1.addString(def.getName());

        MessageBuilder builder2 = new MessageBuilder(null, null);
        int paramIdx = 0;
        for (BlockPart part : def.getParts()) {
            if (part instanceof ParamPart) {
                ParamPart p = (ParamPart) part;
                p.writeInst(builder2, params[paramIdx++]);
            }
        }

        // Don't get corrupted if params change
        byte[] data = builder2.getData();
        builder1.addInt(data.length);
        builder1.addRaw(data);
    }

    public Command toCommand(Robot robot) {
        return def.getCreator().apply(params, robot);
    }
}
