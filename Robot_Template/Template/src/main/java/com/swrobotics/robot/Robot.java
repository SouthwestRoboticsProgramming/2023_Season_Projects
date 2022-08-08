package com.swrobotics.robot;

import com.swrobotics.robot.input.Input;
import com.swrobotics.robot.subsystem.Drive;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.wpilib.AbstractRobot;

public final class Robot extends AbstractRobot {
    private static final double PERIODIC_PER_SECOND = 50;

    public Robot() {
	    super(PERIODIC_PER_SECOND);

	    // Provide connection parameters and enable Messenger
        // Important: This does not prevent the code from working without Messenger!
        //            It only tells the Messenger client to try to connect.
	    initMessenger("localhost", 5805, "Robot");
    }
    
    @Override
    protected final void addSubsystems() {
        Input input = new Input();
        Drive drive = new Drive(input);

        Scheduler scheduler = Scheduler.get();
        scheduler.addSubsystem(drive);
//        scheduler.addSubsystem(new TestSubsystem());

        new NTDouble("Some Category/Some Subcategory/Some Value", 123).set(123.0);
    }
}
