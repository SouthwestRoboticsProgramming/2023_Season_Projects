package com.team2129.lib.schedule;

import java.util.Collections;

import com.team2129.lib.schedule.debug.CommandDebugDesc;
import com.team2129.lib.schedule.debug.CompoundCommandDebugCallback;

public final class CommandLoop implements CompoundCommand {
    private final Command cmd;
    private int repeats;
    
    public CommandLoop(Command cmd, int repeats) {
        this.cmd = cmd;
        this.repeats = repeats;
    }

    @Override
    public void init() {
        cmd.init();
    }

    @Override
    public boolean run() {
        if (!cmd.run())
            return false;

        cmd.end(false);
        repeats--;

        if (repeats > 0) {
            cmd.init();
            return false;
        }

        return true;
    }

    @Override
    public void end(boolean cancelled) {
        if (repeats > 0) {
            cmd.end(true);
        }
    }

    @Override
    public void setDebugCallback(CompoundCommandDebugCallback cb) {
        cb.onChildrenInfoChanged(Collections.singletonList(new CommandDebugDesc(cmd, true)));
    }
}
