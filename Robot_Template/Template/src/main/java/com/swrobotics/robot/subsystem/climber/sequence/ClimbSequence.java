package com.swrobotics.robot.subsystem.climber.sequence;

import com.swrobotics.robot.Robot;
import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.climber.Climber;
import com.swrobotics.robot.subsystem.climber.ClimberStep;
import com.team2129.lib.gyro.Gyroscope;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.CommandSequence;
import com.team2129.lib.wpilib.RobotState;

public class ClimbSequence extends CommandSequence {

    private static final String PATH = "Climber/Steps/";

    // Steps : All angles are clockwise degrees
    private static final NTDouble START_TELE = new NTDouble(PATH + "Start Tele" , 0.05);

    private static final NTDouble ARMS_UP_TO_MID_TELE = new NTDouble(PATH + "Arms up to mid Tele", 1.0);
    private static final NTDouble PULL_UP_TELE = new NTDouble(PATH + "Pull up Tele", 0.04);
    private static final NTDouble RELEASE_TELE = new NTDouble(PATH + "Release from bar Tele", 0.4);
    private static final NTDouble REACH_TO_NEXT_BAR_TELE = new NTDouble(PATH + "Extend to next bar Tele", 0.9);

    private static final NTDouble LOCK_IN_ROT = new NTDouble(PATH + "Lock In Rot", 94.0);
    private static final NTDouble GET_OUT_OF_THE_WAY_ROT = new NTDouble(PATH + "Get out of the way Rot", 110.0);
    private static final NTDouble LEAN_BACK_ROT = new NTDouble(PATH + "Lean back Rot", 57.0);
    private static final NTDouble PRESSURE_ROT = new NTDouble(PATH + "Pressure on next bar Rot", 69.0);

    private final Climber climber;
    private final Input input;

    public ClimbSequence(Climber climber, /*Gyroscope gyro,*/ Input input) {
        this.climber = climber;
        this.input = input;

        // Ground to mid
        append(new ClimberStep(climber, START_TELE, GET_OUT_OF_THE_WAY_ROT, false)); // Arms down, ready to start
        append(new ClimberStep(climber, ARMS_UP_TO_MID_TELE, GET_OUT_OF_THE_WAY_ROT, false)); // Raise arms to reach to mid bar
        append(new ClimberStep(climber, PULL_UP_TELE, GET_OUT_OF_THE_WAY_ROT, true)); // Pull robot up to mid bar
        append(new ClimberStep(climber, PULL_UP_TELE, LOCK_IN_ROT, true)); // Lock rotators into mid bar
        append(new ClimberStep(climber, RELEASE_TELE, LEAN_BACK_ROT, true)); // Lean back to reach to next bar
        // append(new WaitForAngleCommand(gyro)); // Make sure we're at the right angle

        // Mid to high
        append(new ClimberStep(climber, REACH_TO_NEXT_BAR_TELE, LEAN_BACK_ROT, false)); // Extend arms to reach up to high bar
        append(new ClimberStep(climber, REACH_TO_NEXT_BAR_TELE, PRESSURE_ROT, true)); // Rotate to put pressure on high bar
        append(new ClimberStep(climber, PULL_UP_TELE, GET_OUT_OF_THE_WAY_ROT, true)); // Pull up to high bar
        append(new ClimberStep(climber, PULL_UP_TELE, LOCK_IN_ROT, true)); // Lock rotators into high bar
        append(new ClimberStep(climber, RELEASE_TELE, LEAN_BACK_ROT, true)); // Lean back to traversal bar
        // append(new WaitForAngleCommand(gyro)); // Make sure we're at the right angle

        // High to traversal
        append(new ClimberStep(climber, REACH_TO_NEXT_BAR_TELE, LEAN_BACK_ROT, false)); // Extend arms to reach up to traversal bar
        append(new ClimberStep(climber, REACH_TO_NEXT_BAR_TELE, PRESSURE_ROT, true)); // Rotate to put pressure on traversal bar
        append(new ClimberStep(climber, PULL_UP_TELE, GET_OUT_OF_THE_WAY_ROT, true)); // Pull up to traversal bar
        append(new ClimberStep(climber, PULL_UP_TELE, LOCK_IN_ROT, true)); // Lock rotators into traversal bar
        // append(new ClimberStep(climber, RELEASE_TELE, LEAN_BACK_ROT, true)); // Lean back to release intake
    }

    @Override
    public boolean run() {
        boolean superOut = super.run();

        if (Robot.get().getCurrentState() == RobotState.DISABLED) {
            goTo(0);
        }
        
        if (!(getCurrent() instanceof WaitForAngleCommand) && climber.inTolerance() && input.getClimbNextStep()) {
            next();
        }

        if (input.getClimbPreviousStep()) {
            back();
        }

        return superOut;
    }
}
