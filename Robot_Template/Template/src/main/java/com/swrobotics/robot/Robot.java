package com.swrobotics.robot;

import com.swrobotics.lib.AbstractRobot;
import com.swrobotics.robot.subsystem.Drive;

public final class Robot extends AbstractRobot {
    private static final double PERIODIC_PER_SECOND = 50;

    public Robot() {
	super(PERIODIC_PER_SECOND);
    }
    
    @Override
    protected final void addSubsystems() {
        // Add subsystems here
        new Drive();
    }
}
