package com.swrobotics.robot.test;

import com.team2129.lib.schedule.Command;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

public final class TestSystem implements Subsystem {
    private Command cmd;

    @Override
    public void testInit() {
        cmd = new TestSequence();
        Scheduler.get().addCommand(this, cmd);
    }

    private void cancelCommand() {
        if (cmd != null) {
            Scheduler.get().removeCommand(cmd);
        }
        cmd = null;
    }

    @Override
    public void teleopInit() {
        cancelCommand();
    }

    @Override
    public void disabledInit() {
        cancelCommand();
    }

    @Override
    public void autonomousInit() {
        cancelCommand();
    }
}
