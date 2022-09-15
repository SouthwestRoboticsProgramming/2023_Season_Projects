package com.team2129.lib.schedule;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;

public abstract class CommandSequence implements Command {
    private final List<Command> cmds;
    private int index;

    public CommandSequence() {
        cmds = new ArrayList<Command>();
    }

    protected void append(Command cmd) {
        cmds.add(cmd);
    }

    protected Command getCurrent() {
        if (running()) {
            return cmds.get(index);
        } else {
            return null;
        }
    }

    @Override
    public void init() {
        DriverStation.reportError("Empty CommandSequence", cmds.isEmpty());

        index = 0;
        cmds.get(index).init();
    }

    public void next() {
        cmds.get(index).end(true);
        index++;

        if (running()) {
            cmds.get(index).init();
        }
    }

    public void back() {
        cmds.get(index).end(true);
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

        return !running();
    }

    @Override
    public void end(boolean wasCanceled) {
        if (running()) {
            cmds.get(index).end(true);
        }
    }


    private boolean running() {
        return index >=0 && index < cmds.size();
    }
}
