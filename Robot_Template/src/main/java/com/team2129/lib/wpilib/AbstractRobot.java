package com.team2129.lib.wpilib;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.profile.ProfileNode;
import com.team2129.lib.profile.Profiler;
import com.team2129.lib.time.Duration;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.messenger.ReadMessages;
import com.team2129.lib.schedule.Scheduler;

import com.team2129.lib.time.Repeater;
import com.team2129.lib.time.TimeUnit;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;

public abstract class AbstractRobot extends RobotBase {
    public static boolean isSimulation() {
        return !isReal();
    }

    private static AbstractRobot INSTANCE = null;
    public static AbstractRobot get() {
        return INSTANCE;
    }

    private final double periodicPerSecond;

    private MessengerClient msg;
    private volatile boolean running;

    public AbstractRobot(double periodicPerSecond) {
        this.periodicPerSecond = periodicPerSecond;

        if (INSTANCE != null)
            throw new IllegalStateException("Robot already initialized");
        INSTANCE = this;

        running = false;
    }

    protected abstract void addSubsystems();

    protected final void initMessenger(String host, int port, String name) {
        msg = new MessengerClient(host, port, name);
        Scheduler.get().addSubsystem(new ReadMessages(msg));
        Scheduler.get().initMessenger(msg);
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

        // Initialize periodic repeater
        Repeater repeater = new Repeater(
                new Duration(1 / periodicPerSecond, TimeUnit.SECONDS),
                () -> {
                    Profiler.beginMeasurements("Root");

                    RobotState state = getCurrentState();
                    Scheduler.get().periodicState(state);

                    Profiler.endMeasurements();
                    if (msg != null) {
                        MessageBuilder builder = msg.prepare("Profiler:Data");
                        encodeProfileNode(builder, Profiler.getLastData());
                        builder.send();
                    }
                }
        );

        Thread currentThread = Thread.currentThread();
        while (running && !currentThread.isInterrupted()) {
            repeater.tick();
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

    public final double getPeriodicPerSecond() {
        return periodicPerSecond;
    }

    private void encodeProfileNode(MessageBuilder builder, ProfileNode node) {
        builder.addString(node.getName());
        builder.addLong(node.getSelfTimeNanoseconds());
        builder.addLong(node.getTotalTimeNanoseconds());
        builder.addInt(node.getChildren().size());
        for (ProfileNode child : node.getChildren())
            encodeProfileNode(builder, child);
    }
}
