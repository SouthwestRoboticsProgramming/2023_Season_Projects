package com.swrobotics.robot;

import com.swrobotics.lib.gyro.NavX;
import com.swrobotics.lib.messenger.MessengerClient;
import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.wpilib.AbstractRobot;
import com.swrobotics.robot.auto.AutoSystem;
import com.swrobotics.robot.auto.Pathfinder;
import com.swrobotics.robot.blockauto.AutoBlocks;
import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.Intake;
import com.swrobotics.robot.subsystem.Localization;
import com.swrobotics.robot.subsystem.drive.Drive;
import com.swrobotics.robot.subsystem.thrower.Thrower;
import com.swrobotics.robot.test.TestSystem;

public final class Robot extends AbstractRobot {
    private static final double PERIODIC_PER_SECOND = 50;
    private static final String RASPBERRY_PI_IP = "10.21.29.3";

    private Localization loc;
    private Pathfinder pathfinder;
    private Drive drive;
    private Thrower thrower;

    public Robot() {
	    super(PERIODIC_PER_SECOND);

	    // Provide connection parameters and enable Messenger
        // Important: This does not prevent the code from working without Messenger!
        //            It only tells the Messenger client to try to connect.
	    if (isSimulation()) {
            // Connect to local Messenger server
            initMessenger("localhost", 5805, "Robot");
        } else {
            // Connect to Raspberry Pi
            initMessenger(RASPBERRY_PI_IP, 5805, "Robot");
        }

        AutoBlocks.init(getMessenger(), this);
    }
    
    @Override
    protected final void addSubsystems() {
        // Common I/O that is not a subsystem
        MessengerClient msg = getMessenger();
        Input input = new Input();
        NavX gyro = new NavX(); // Prefer using odometry angle rather than gyro for most things

        // Note: Do not instantiate a subsystem without adding it to
        //     the scheduler, it could cause unexpected behavior and
        //     will cause a warning to be printed if any motors are
        //     used in it

        drive = new Drive(input, gyro, msg);
        loc = new Localization(drive);
        pathfinder = new Pathfinder(msg, loc);

        Intake intake = new Intake(input);
        thrower = new Thrower(input, loc);

        AutoSystem auto = new AutoSystem();
        TestSystem test = new TestSystem();

        Scheduler scheduler = Scheduler.get();
        scheduler.addSubsystem(loc);
        scheduler.addSubsystem(pathfinder);
        scheduler.addSubsystem(drive);
        scheduler.addSubsystem(intake);
        scheduler.addSubsystem(thrower);

        scheduler.addSubsystem(auto);
        scheduler.addSubsystem(test);
    }

    public Localization getLocalization() {
        return loc;
    }

    public Pathfinder getPathfinder() {
        return pathfinder;
    }

    public Drive getDrive() {
        return drive;
    }

    public Thrower getThrower() {
        return thrower;
    }
}
