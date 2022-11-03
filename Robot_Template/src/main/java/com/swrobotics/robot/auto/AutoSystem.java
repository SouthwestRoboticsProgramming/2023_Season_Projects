package com.swrobotics.robot.auto;

import com.swrobotics.robot.blockauto.AutoBlocks;
import com.team2129.lib.schedule.Command;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

public final class AutoSystem implements Subsystem {
    private Command cmd;

    @Override
    public void autonomousInit() {
        cmd = AutoBlocks.getSelectedAutoCommand();
        if (cmd != null)
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
    public void testInit() {
        cancelCommand();
    }
}
