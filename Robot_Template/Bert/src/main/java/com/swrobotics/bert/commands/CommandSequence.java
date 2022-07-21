package com.swrobotics.bert.commands;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandSequence implements Command {
    private final List<Command> cmds;
    private int index;

    public CommandSequence() {
        cmds = new ArrayList<>();
    }

    protected void append(Command cmd) {
        cmds.add(cmd);
    }

    protected Command getCurrent() {
        if (running())
            return cmds.get(index);
        else
            return null;
    }

    @Override
    public void init() {
        if (cmds.size() == 0) {
            throw new IllegalStateException("Empty CommandSequence");
        }

        index = 0;
        cmds.get(index).init();
    }

    public void next() {
        cmds.get(index).end();
        index++;

        if (running()) {
            cmds.get(index).init();
        }
    }

    public void back() {
        cmds.get(index).end();
        index--;

        if (running()) {
            cmds.get(index).init();
        }
    }

    public void goTo(int index) {
        this.index = index;
    }

    @Override
    public boolean run() {
        if (!running()) return true;

        if (cmds.get(index).run()) {
            next();
        }

        return false;
    }

    @Override
    public void end() {
        if (running()) {
            cmds.get(index).end();
        }
    }

    private boolean running() {
        return index >= 0 && index < cmds.size();
    }
}
