package com.swrobotics.robot;

import com.swrobotics.robot.input.Input;
import com.swrobotics.robot.subsystem.Drive;
import com.swrobotics.robot.subsystem.TestSubsystem;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.wpilib.AbstractRobot;

public final class Robot extends AbstractRobot {
    private static final double PERIODIC_PER_SECOND = 50;

    public Robot() {
	super(PERIODIC_PER_SECOND);
    }
    
    @Override
    protected final void addSubsystems() {
        Input input = new Input();
        Drive drive = new Drive(input);

        Scheduler scheduler = Scheduler.get();
        scheduler.addSubsystem(drive);
//        scheduler.addSubsystem(new TestSubsystem());
    }
}
