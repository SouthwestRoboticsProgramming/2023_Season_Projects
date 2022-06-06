package com.swrobotics.robot;

import com.swrobotics.lib.RobotState;
import com.swrobotics.lib.routine.Scheduler;
import com.swrobotics.robot.input.Input;
import com.swrobotics.robot.subsystem.MiniRobotDrive;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;

public final class Robot extends RobotBase {
    private static final double PERIODIC_PER_SECOND = 50;

    private boolean running = false;

    private Input input;
    private MiniRobotDrive drive;

    private void addSubsystems() {
        // Add subsystems here
        input = new Input();
        drive = new MiniRobotDrive(input);
    }

    @Override
    public void startCompetition() {
        // TODO: Move this logic to the library

        running = true;

        addSubsystems();

        System.out.println("****** Robot program startup complete ******");
        HAL.observeUserProgramStarting();

        long lastTime = System.nanoTime();
        double secondsPerPeriodic = 1.0 / PERIODIC_PER_SECOND;
        double unprocessedTime = 0;

        RobotState lastState = RobotState.DISABLED;
        while (running && !Thread.currentThread().isInterrupted()) {
            long currentTime = System.nanoTime();
            unprocessedTime += (currentTime - lastTime) / 1_000_000_000.0;
            lastTime = currentTime;

            while (unprocessedTime > secondsPerPeriodic) {
                unprocessedTime -= secondsPerPeriodic;

                Scheduler.get().periodic();

                RobotState state = getCurrentState();

                if (state != lastState) {
                    Scheduler.get().onStateSwitch(state);
                }
                lastState = state;
            }
        }
    }

    @Override
    public void endCompetition() {
        running = false;
    }

    public RobotState getCurrentState() {
        if (isDisabled()) return RobotState.DISABLED;
        if (isAutonomous()) return RobotState.AUTONOMOUS;
        if (isTeleop()) return RobotState.TELEOP;
        if (isTest()) return RobotState.TEST;

        throw new IllegalStateException("Illegal robot state");
    }
}
