package com.team2129.lib.wpilib;

import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.messenger.MessengerReadRoutine;
import com.team2129.lib.routine.Scheduler;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;

public abstract class AbstractRobot extends RobotBase {
    private static AbstractRobot INSTANCE = null;
    public static AbstractRobot get() {
        return INSTANCE;
    }

    private final double periodicPerSecond;

    private MessengerClient msg;
    private boolean running = false;

    public AbstractRobot(double periodicPerSecond) {
        this.periodicPerSecond = periodicPerSecond;

        if (INSTANCE != null)
            throw new IllegalStateException("Robot already initialized");
        INSTANCE = this;
    }

    protected abstract void addSubsystems();

    protected final void initMessenger(String host, int port, String name) {
        msg = new MessengerClient(host, port, name);
        Scheduler.get().addRoutine(new MessengerReadRoutine(msg));
        Scheduler.get().registerMessengerQueryHook(msg);
    }

    public boolean hasMessenger() {
        return msg != null;
    }

    public MessengerClient getMessenger() {
        return msg;
    }

    @Override
    public final void startCompetition() {
        running = true;
        addSubsystems();

        System.out.println("**** Robot program startup complete ****");
        HAL.observeUserProgramStarting();

        long lastTime = System.nanoTime();
        double secondsPerPeriodic = 1.0 / periodicPerSecond;
        double unprocessedTime = 0;

        // The robot always starts disabled
        RobotState lastState = RobotState.DISABLED;
        Scheduler.get().onStateSwitch(RobotState.DISABLED);

        Thread currentThread = Thread.currentThread();
        while (running && !currentThread.isInterrupted()) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastTime;
            unprocessedTime += elapsedTime / 1_000_000_000.0;
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
    public final void endCompetition() {
        running = false;
    }

    public final RobotState getCurrentState() {
        if (isDisabled()) return RobotState.DISABLED;
        if (isAutonomous()) return RobotState.AUTONOMOUS;
        if (isTeleop()) return RobotState.TELEOP;
        if (isTest()) return RobotState.TEST;

        throw new IllegalStateException("Illegal robot state");
    }
}
