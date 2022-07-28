package com.team2129.lib.schedule.command;

public interface Command {
    default void init() {}

    // Returns true when complete
    boolean run();

    default void end(boolean wasCancelled) {}
}
