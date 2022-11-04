package com.team2129.lib.wpilib;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.messenger.ReadMessages;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.time.Duration;
import com.team2129.lib.time.Repeater;
import com.team2129.lib.time.TimeUnit;
import com.team2129.lib.profile.ProfileNode;
import com.team2129.lib.profile.Profiler;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * The base class for any robot using this library.
 */
public abstract class AbstractRobot extends RobotBase {
    /**
     * Gets whether the robot code is currently running in a simulation.
     * 
     * @return whether in a simulation
     */
    public static boolean isSimulation() {
        return !isReal();
    }

    private static AbstractRobot INSTANCE = null;

    /**
     * Gets the one robot instance.
     * 
     * @return instance
     */
    public static AbstractRobot get() {
        return INSTANCE;
    }

    private final double periodicPerSecond;

    private MessengerClient msg;
    private volatile boolean running;

    /**
     * Initializes the robot and the library.
     * 
     * @param periodicPerSecond desired number of periodic invocations per second
     */
    public AbstractRobot(double periodicPerSecond) {
        this.periodicPerSecond = periodicPerSecond;

        if (INSTANCE != null)
            throw new IllegalStateException("Robot already initialized");
        INSTANCE = this;

        running = false;
    }

    /**
     * This method is where your robot code should schedule its subsystems.
     */
    protected abstract void addSubsystems();

    /**
     * Initializes a Messenger client with the given connection parameters.
     * 
     * @param host host of the Messenger server
     * @param port port of the Messenger server
     * @param name name of this client
     */
    protected final void initMessenger(String host, int port, String name) {
        msg = new MessengerClient(host, port, name);
        Scheduler.get().addSubsystem(new ReadMessages(msg));
        Scheduler.get().initMessenger(msg);
    }

    /**
     * Gets whether this robot has a Messenger client.
     * 
     * @return whether has messenger
     */
    public boolean hasMessenger() {
        return msg != null;
    }

    /**
     * Gets the Messenger client.
     * 
     * @return messenger client
     */
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

    /**
     * Gets the current state of the robot.
     * 
     * @return current state
     */
    public final RobotState getCurrentState() {
        if (isDisabled()) return RobotState.DISABLED;
        if (isAutonomous()) return RobotState.AUTONOMOUS;
        if (isTeleop()) return RobotState.TELEOP;
        if (isTest()) return RobotState.TEST;

        throw new IllegalStateException("Illegal robot state");
    }

    /**
     * Gets the number of periodic invocations per second the main loop is
     * running at.
     * 
     * @return periodic per second
     */
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
