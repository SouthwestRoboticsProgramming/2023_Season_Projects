package com.swrobotics.bert.commands;

public interface Command {
    void init();

    // Return whether the command is done
    boolean run();

    void end();

    default int getInterval() {
        return 1;
    }
}
