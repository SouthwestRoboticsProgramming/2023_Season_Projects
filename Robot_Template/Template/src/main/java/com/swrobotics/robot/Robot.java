package com.swrobotics.robot;

import com.swrobotics.robot.blockauto.AutoBlocks;
import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.climber.TelescopingArm;
import com.swrobotics.robot.subsystem.climber.rotating.RotatingArm;
import com.swrobotics.robot.subsystem.climber.rotating.RotatingArms;
import com.swrobotics.robot.subsystem.drive.Drive;
import com.team2129.lib.gyro.NavX;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.wpilib.AbstractRobot;

public final class Robot extends AbstractRobot {
    private static final double PERIODIC_PER_SECOND = 50;
    private static final String RASPBERRY_PI_IP = "10.21.29.3";

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

        AutoBlocks.init(getMessenger());
    }
    
    @Override
    protected final void addSubsystems() {
        // Common I/O that is not a subsystem
        // MessengerClient msg = getMessenger();
        Input input = new Input();
        // NavX gyro = new NavX(); // Prefer using odometry angle rather than gyro for most things

        // Note: Do not instantiate a subsystem without adding it to
        //     the scheduler, it could cause unexpected behavior and
        //     will cause a warning to be printed if any motors are
        //     used in it

        // Drive drive = new Drive(input, gyro, msg);

        // TelescopingArm tele = new TelescopingArm(6, 7, false);
        // RotatingArm rotating = new RotatingArm(10);

        Scheduler scheduler = Scheduler.get();
        // scheduler.addSubsystem(tele);
        scheduler.addSubsystem(rotating);
    }
}
