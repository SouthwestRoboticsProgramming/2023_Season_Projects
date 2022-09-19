package com.team2129.lib.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.team2129.lib.schedule.debug.CommandDebugDesc;
import com.team2129.lib.schedule.debug.CompoundCommandDebugCallback;

/**
 * Utility command to run multiple commands in parallel.
 */
public class CommandUnion implements CompoundCommand {
    private static final class CommandWrapper {
        Command cmd;
        boolean finished;
    }

    private final List<CommandWrapper> children;
    private boolean hasInitialized;
    private CompoundCommandDebugCallback debug;

    public CommandUnion(Command... children) {
        this(Arrays.asList(children));
    }

    public CommandUnion(List<Command> children) {
        this.children = new ArrayList<>();
        hasInitialized = false;
        for (Command child : children) {
            add(child);
        }
    }

    public void add(Command child) {
        CommandWrapper w = new CommandWrapper();
        w.cmd = child;
        w.finished = false;
        children.add(w);

        if (hasInitialized)
            child.init();

        invokeDebugCallback();
    }

    @Override
    public void init() {
        hasInitialized = true;
        for (CommandWrapper w : children) {
            w.cmd.init();
        }
        invokeDebugCallback();
    }

    @Override
    public boolean run() {
        boolean anyRunning = false;
        for (CommandWrapper w : children) {
            if (!w.finished) {
                w.finished = w.cmd.run();
                if (w.finished) {
                    w.cmd.end(false);
                    invokeDebugCallback();
                } else {
                    anyRunning = true;
                }
            }
        }
        return !anyRunning;
    }

    @Override
    public void end(boolean cancelled) {
        for (CommandWrapper w : children) {
            if (!w.finished) {
                w.cmd.end(cancelled);
            }
        }
    }

    private void invokeDebugCallback() {
        if (debug == null)
            return;

        List<CommandDebugDesc> out = new ArrayList<>();
        for (CommandWrapper w : children) {
            out.add(new CommandDebugDesc(w.cmd, !w.finished));
        }
        debug.onChildrenInfoChanged(out);
    }

    @Override
    public void setDebugCallback(CompoundCommandDebugCallback cb) {
        debug = cb;
        invokeDebugCallback();
    }
}
