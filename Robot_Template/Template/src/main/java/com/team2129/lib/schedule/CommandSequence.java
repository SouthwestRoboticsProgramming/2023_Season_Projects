package com.team2129.lib.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandSequence implements Command {
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
    }

    private final List<CommandWrapper> cmds;
    private int index;

    public CommandSequence(Command... cmds) {
        this(Arrays.asList(cmds));
    }

    public CommandSequence(List<Command> cmds) {
        this.cmds = new ArrayList<>();
        for (Command cmd : cmds) {
            append(cmd);
        }
    }

    public void append(Command cmd) {
        CommandWrapper w = new CommandWrapper();
        w.cmd = cmd;
        cmds.add(w);
    }

    @Override
    public void init() {
        index = 0;
        if (!cmds.isEmpty())
            cmds.get(0).cmd.init();
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
    }

    public void back() {
        if (running())
            cmds.get(index).end();
        index--;
        if (running())
            cmds.get(index).init();
    }

    public void goTo(int dst) {
        if (running())
            cmds.get(index).end();
        index = dst;
        if (running())
            cmds.get(index).init();
    }

    @Override
    public boolean run() {
        if (!running()) return true; // Should never happen, but just in case

        if (cmds.get(index).run()) {
            next();
        }

        return !running();
    }
}
