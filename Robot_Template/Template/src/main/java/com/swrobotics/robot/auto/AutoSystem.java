package com.swrobotics.robot.auto;

import com.swrobotics.robot.subsystem.drive.Drive;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

public final class AutoSystem implements Subsystem {
    private final Drive drive;
    private AutoSequence cmd;

    public AutoSystem(Drive drive) {
        this.drive = drive;
        cmd = null;
    }

    @Override
    public void autonomousInit() {
        cmd = new AutoSequence(drive);
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
