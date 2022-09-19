package com.swrobotics.robot.blockauto;

import java.util.function.Function;

import com.team2129.lib.schedule.Command;

/**
 * An instance of a {@link BlockDef}.
 */
public final class BlockInst {
    private final Function<Object[], Command> creator;
    private final Object[] params;

    public BlockInst(Function<Object[], Command> creator, Object[] params) {
        this.creator = creator;
        this.params = params;
    }

    public Command toCommand() {
        return creator.apply(params);
    }
}
