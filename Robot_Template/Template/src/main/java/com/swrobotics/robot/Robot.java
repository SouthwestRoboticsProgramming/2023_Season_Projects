package com.swrobotics.robot;

import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.Intake;
import com.swrobotics.robot.subsystem.Limelight;
import com.swrobotics.robot.subsystem.Localization;
import com.swrobotics.robot.subsystem.drive.Drive;
import com.swrobotics.robot.subsystem.thrower.BallDetector;
import com.swrobotics.robot.subsystem.thrower.Flywheel;
import com.swrobotics.robot.subsystem.thrower.Hood;
import com.swrobotics.robot.subsystem.thrower.Hopper;
import com.swrobotics.robot.subsystem.thrower.Thrower;
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
    }
    
    @Override
    protected final void addSubsystems() {
        Input input = new Input();
        Drive drive = new Drive(input);
        Limelight limelight = new Limelight();
        Localization loc = new Localization(drive, limelight);
        // TODO: PDP
        // TODO: Light controller
        BallDetector ballDetector = new BallDetector();
        Intake intake = new Intake(input);
        Thrower thrower = new Thrower(input, loc);
        // TODO: Climber

        Scheduler scheduler = Scheduler.get();
        scheduler.addSubsystem(drive);
        scheduler.addSubsystem(thrower);
        // TODO: Schedule subsystems
    }
}
