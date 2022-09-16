package com.team2129.lib.schedule;

public final class CommandDebugDesc {
    private final Command cmd;
    private final boolean active;

    public CommandDebugDesc(Command cmd, boolean active) {
        this.cmd = cmd;
        this.active = active;
    }

    public Command getCommand() {
        return cmd;
    }

    public boolean isActive() {
        return active;
    }
}
