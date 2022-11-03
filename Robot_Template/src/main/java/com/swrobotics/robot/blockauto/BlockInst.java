package com.swrobotics.robot.blockauto;

import java.util.function.Function;

import com.swrobotics.robot.Robot;
import com.swrobotics.robot.blockauto.part.BlockPart;
import com.swrobotics.robot.blockauto.part.ParamPart;
import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.schedule.Command;

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

    public void write(MessageBuilder builder) {
        builder.addString(def.getName());
        int paramIdx = 0;
        for (BlockPart part : def.getParts()) {
            if (part instanceof ParamPart) {
                ParamPart p = (ParamPart) part;
                p.writeInst(builder, params[paramIdx++]);
            }
        }
    }

    public Command toCommand(Robot robot) {
        return def.getCreator().apply(params, robot);
    }
}
