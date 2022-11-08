package com.swrobotics.robot.test;

import com.swrobotics.lib.schedule.Command;
import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.schedule.Subsystem;

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
