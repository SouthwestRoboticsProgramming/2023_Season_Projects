package com.swrobotics.robot.auto;

import com.swrobotics.robot.subsystem.drive.Drive;
import com.swrobotics.robot.subsystem.thrower.Hopper;
import com.swrobotics.robot.subsystem.thrower.commands.ShootCommand;
import com.team2129.lib.schedule.Command;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

public final class AutoSystem implements Subsystem {
    private final Drive drive;
    private final Hopper hopper;
    private Command cmd;

    public AutoSystem(Drive drive, Hopper hopper) {
        this.drive = drive;
        this.hopper = hopper;
        cmd = null;
    }

    @Override
    public void autonomousInit() {
        cmd = new AutoSequence(drive, hopper);
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
