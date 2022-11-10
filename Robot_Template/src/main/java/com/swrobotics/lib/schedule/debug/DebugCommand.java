package com.swrobotics.lib.schedule.debug;

import com.swrobotics.lib.schedule.Command;

/**
 * Debugging command that logs all method calls.
 */
public final class DebugCommand implements Command {
    private final String name;
    private int countdown;

    /**
     * @param name name in logging
     * @param time number of cycles to run for
     */
    public DebugCommand(String name, int time) {
        this.name = name;
        countdown = time;
    }

    @Override
    public void init() {
        System.out.println("Command " + name + " init");
    }

    @Override
    public boolean run() {
        System.out.println("Command " + name + " run");
        return --countdown <= 0;
    }

    @Override
    public void end(boolean wasCancelled) {
        System.out.println("Command " + name + " end (cancelled: " + wasCancelled + ")");
    }

    @Override
    public void suspend() {
        System.out.println("Command " + name + " suspended");
    }

    @Override
    public void resume() {
        System.out.println("Command " + name + " resumed");
    }
}
