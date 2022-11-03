package com.team2129.lib.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.team2129.lib.schedule.debug.CommandDebugDesc;
import com.team2129.lib.schedule.debug.CompoundCommandDebugCallback;

public class CommandSequence implements CompoundCommand {
    // TODO: Maybe make this its own class since we use the same thing
    //       in both CommandSequence and CommandUnion
    private static final class CommandWrapper {
        Command cmd;
        boolean finished;

        void init() {
            finished = false;
            cmd.init();
        }

        boolean run() {
            finished = cmd.run();
            if (finished)
                cmd.end(false);
            return finished;
        }

        void end() {
            if (!finished) {
                cmd.end(true);
            }
        }

        Command getCommand() {
            return cmd;
        }
    }

    private final List<CommandWrapper> cmds;
    private int index;
    private CompoundCommandDebugCallback debug;

    public CommandSequence(Command... cmds) {
        this(Arrays.asList(cmds));
    }

    public CommandSequence(List<Command> cmds) {
        this.cmds = new ArrayList<>();
        for (Command cmd : cmds) {
            appendInternal(cmd);
        }
        // Debug callback is not yet set
    }

    public void append(Command cmd) {
        appendInternal(cmd);
        invokeDebugCallback();
    }

    private void appendInternal(Command cmd) {
        CommandWrapper w = new CommandWrapper();
        w.cmd = cmd;
        cmds.add(w);
    }

    @Override
    public void init() {
        index = 0;
        if (!cmds.isEmpty())
            cmds.get(0).cmd.init();

        invokeDebugCallback();
    }

    private boolean running() {
        return index >= 0 && index < cmds.size();
    }

    public void next() {
        if (running())
            cmds.get(index).end();
        index++;
        if (running())
            cmds.get(index).init();

        invokeDebugCallback();
    }

    public void back() {
        if (running())
            cmds.get(index).end();
        index--;
        if (running())
            cmds.get(index).init();

        invokeDebugCallback();
    }

    public void goTo(int dst) {
        if (running())
            cmds.get(index).end();
        index = dst;
        if (running())
            cmds.get(index).init();

        invokeDebugCallback();
    }

    public Command getCurrent() {
        return cmds.get(index).getCommand();
    }

    @Override
    public boolean run() {
        if (!running()) return true; // Should never happen, but just in case

        if (cmds.get(index).run()) {
            next();
        }

        return !running();
    }

    private void invokeDebugCallback() {
        if (debug == null)
            return;

        List<CommandDebugDesc> out = new ArrayList<>();
        for (int i = 0; i < cmds.size(); i++) {
            out.add(new CommandDebugDesc(cmds.get(i).cmd, i == index));
        }
        debug.onChildrenInfoChanged(out);
    }

    @Override
    public void setDebugCallback(CompoundCommandDebugCallback cb) {
        debug = cb;
        invokeDebugCallback();
    }
}
