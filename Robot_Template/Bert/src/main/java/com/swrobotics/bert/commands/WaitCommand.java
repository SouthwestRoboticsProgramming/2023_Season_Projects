package com.swrobotics.bert.commands;

import static com.swrobotics.bert.constants.Constants.*;

public final class WaitCommand implements Command {
    private int timer;

    public WaitCommand(double seconds) {
        timer = (int) (PERIODIC_PER_SECOND * seconds);
    }

    @Override
    public void init() {

    }

    @Override
    public boolean run() {
        return --timer <= 0;
    }

    @Override
    public void end() {
        
    }
}
