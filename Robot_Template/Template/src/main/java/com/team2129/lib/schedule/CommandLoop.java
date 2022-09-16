package com.team2129.lib.schedule;

public final class CommandLoop implements Command {
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
}
