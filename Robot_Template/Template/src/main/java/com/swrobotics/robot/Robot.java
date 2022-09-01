package com.swrobotics.robot;

import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.Limelight;
import com.swrobotics.robot.subsystem.Localization;
import com.swrobotics.robot.subsystem.drive.Drive;
import com.swrobotics.robot.subsystem.thrower.Thrower;
import com.team2129.lib.schedule.Command;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;
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
        Localization loc = new Localization(drive, limelight, input);
        // TODO: PDP
        // TODO: Light controlle
        Thrower thrower = new Thrower(input, loc);
        // TODO: Climber

        Scheduler scheduler = Scheduler.get();
        scheduler.addSubsystem(drive);
        scheduler.addSubsystem(thrower);
        // TODO: Schedule subsystems

        // Test short, rapid command adding
        // scheduler.addSubsystem(new Subsystem() {
        //     @Override
        //     public void periodic() {
        //         int[] i = new int[1];
        //         i[0] = 0;
        //         Scheduler.get().addCommand(this, () -> {
        //             // Should only ever print "The command runs: count 0"
        //             System.out.println("The command runs: count " + i[0]++);
        //             return true;
        //         });
        //     }
        // });

        // Test cancelling command
        // scheduler.addSubsystem(new Subsystem() {
        //     int counter = 0;
        //     Command cmd = () -> {
        //         System.out.println("Running the command");
        //         return false;
        //     };

        //     @Override
        //     public void periodic() {
        //         if (counter == 0) {
        //             Scheduler.get().addCommand(this, cmd);
        //             System.out.println("Added the command");
        //         } else if (counter == 50) {
        //             Scheduler.get().removeCommand(cmd);
        //             System.out.println("Removed the command");
        //         }

        //         // Tick counter and wrap back to zero
        //         if (++counter == 100)
        //             counter = 0;
        //     }
        // });
    }
}
