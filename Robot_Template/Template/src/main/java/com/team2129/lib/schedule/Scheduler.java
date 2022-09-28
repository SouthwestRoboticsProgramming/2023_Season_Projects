package com.team2129.lib.schedule;

import com.team2129.lib.wpilib.RobotState;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code Scheduler} is the core of the robot code. It runs
 * {@code Command}s and {@code Subsystem}s every periodic, and
 * manages the starting and stopping of {@code Command}s. The
 * {@code Scheduler} instance is used by {@code AbstractRobot}
 * within the main loop, to make it easier to organize your
 * robot code.
 *
 * @see Command
 * @see Subsystem
 * @see com.team2129.lib.wpilib.AbstractRobot
 */
public final class Scheduler {
    private static final Scheduler INSTANCE = new Scheduler();

    /**
     * Gets the global singleton instance of the {@code Scheduler}.
     *
     * @return singleton instance
     */
    public static Scheduler get() {
        return INSTANCE;
    }

    private interface Node {
        void init(RobotState state);
        void periodic(RobotState state);
    }

    private final class CommandNode implements Node {
        private final Command cmd;
        private boolean initialized, finished;

        public CommandNode(Command cmd) {
            this.cmd = cmd;
            initialized = false;
            finished = false;
        }

        @Override
        public void init(RobotState state) {}

        @Override
        public void periodic(RobotState state) {
            // TODO: Do the interval properly

            if (!initialized) {
                cmd.init();
                initialized = true;
            }

            finished = cmd.run();

            if (finished) {
                cmd.end(false);
                INSTANCE.removeCommand(cmd);
            }
        }
    }

    private final class SubsystemNode implements Node {
        private final Subsystem ss;
        private final List<Node> children;

        public SubsystemNode(Subsystem ss) {
            this.ss = ss;
            children = new ArrayList<>();
        }

        @Override
        public void init(RobotState state) {
            switch (state) {
                case DISABLED:   ss.disabledInit();   break;
                case AUTONOMOUS: ss.autonomousInit(); break;
                case TELEOP:     ss.teleopInit();     break;
                case TEST:       ss.testInit();       break;
            }
        }

        @Override
        public void periodic(RobotState state) {
            switch (state) {
                case DISABLED:   ss.disabledPeriodic();   break;
                case AUTONOMOUS: ss.autonomousPeriodic(); break;
                case TELEOP:     ss.teleopPeriodic();     break;
                case TEST:       ss.testPeriodic();       break;
            }
        }
    }

    private final Map<Command, CommandNode> cmds;
    private final Map<Subsystem, SubsystemNode> subsystems;
    private final List<CommandNode> rootCommands;
    private final List<SubsystemNode> rootSubsystems;

    private Scheduler() {
        cmds = new IdentityHashMap<>();
        subsystems = new IdentityHashMap<>();
        rootCommands = new ArrayList<>();
        rootSubsystems = new ArrayList<>();
    }

    /**
     * Registers a {@code Command} to be executed until it ends or
     * is cancelled using {@link #removeCommand}.
     *
     * @param cmd Command to schedule
     * @throws IllegalStateException if the command is already scheduled
     */
    public void addCommand(Command cmd) {
        addCommand(null, cmd);
    }

    /**
     * Registers a {@code Command} to be executed within a parent
     * {@code Subsystem}. The execution of the command will be
     * suspended if the parent is suspended, and the command will
     * be cancelled if the parent is removed.
     *
     * @param parent parent Subsystem
     * @param cmd Command to schedule
     * @throws IllegalStateException if the command is already scheduled
     */
    public void addCommand(Subsystem parent, Command cmd) {

    }

    /**
     * Registers a {@code Subsystem} to be executed every periodic.
     *
     * @param ss Subsystem to schedule
     * @throws IllegalStateException if the subsystem is already scheduled
     */
    public void addSubsystem(Subsystem ss) {
        addSubsystem(null, ss);
    }

    /**
     * Registers a {@code Subsystem} to be executed every periodic
     * within a parent {@code Subsystem}. The execution of the
     * subsystem will be suspended if the parent is suspended, and
     * will be removed if the parent is removed.
     *
     * @param parent parent Subsystem
     * @param ss Subsystem to schedule
     * @throws IllegalStateException if the subsystem is already scheduled
     */
    public void addSubsystem(Subsystem parent, Subsystem ss) {

    }

    /**
     * Unregisters a {@code Command}, causing it to be cancelled if
     * it is currently running. If the command is running, its
     * execution will be immediately stopped, and the
     * {@link Command#end(boolean)) method will be called.
     *
     * @param cmd Command to cancel
     */
    public void removeCommand(Command cmd) {

    }

    /**
     * Unregisters a {@code Subsystem}, causing its execution to
     * immediately stop. The {@link Subsystem#onRemove()} method
     * will be called, and any registered child {@code Subsystem}s
     * and {@code Command} will also be removed.
     *
     * @param ss Subsystem to remove
     */
    public void removeSubsystem(Subsystem ss) {

    }

    /**
     * Sets whether a {@code Command} is currently suspended. If a
     * command is suspended, it is still scheduled, but its
     * {@link Command#run()} method will not be called.
     *
     * This method will print a warning and have no effect if it is
     * not currently scheduled.
     *
     * @param cmd Command to set suspended state
     * @param suspended whether the command should be suspended
     */
    public void setCommandSuspended(Command cmd, boolean suspended) {

    }

    /**
     * Sets whether a {@code Subsystem} is currently suspended. If a
     * subsystem is suspended, it is still scheduled, but its init and
     * periodic methods are not called. If the robot state is changed
     * while the subsystem is suspended, the subsystem's init method for
     * the new state will be called when it is resumed. When a subsystem
     * is suspended, its child {@code Command}s and {@code Subsystem}s
     * will also be suspended.
     *
     * @param ss Subsystem to set suspended state
     * @param suspended whether the subsystem should be suspended
     */
    public void setSubsystemSuspended(Subsystem ss, boolean suspended) {

    }
}
