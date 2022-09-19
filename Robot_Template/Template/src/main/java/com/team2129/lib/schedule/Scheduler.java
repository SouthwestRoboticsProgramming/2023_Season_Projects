package com.team2129.lib.schedule;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.profile.Profiler;
import com.team2129.lib.wpilib.RobotState;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Scheduler {
    // Message names
    private static final String MSG_QUERY = "Scheduler:Query";
    private static final String MSG_QUERY_RESPONSE = "Scheduler:QueryResponse";
    private static final String MSG_SUBSYSTEM_ADDED = "Scheduler:SubsystemAdded";
    private static final String MSG_SUBSYSTEM_REMOVED = "Scheduler:SubsystemRemoved";
    private static final String MSG_COMMAND_ADDED = "Scheduler:CommandAdded";
    private static final String MSG_COMMAND_REMOVED = "Scheduler:CommandRemoved";
    private static final String MSG_COMMAND_STATUS_CHANGED = "Scheduler:CommandStatusChanged";

    // Node type codes for sending tree
    private static final byte TYPE_CODE_SUBSYSTEM = 0;
    private static final byte TYPE_CODE_COMMAND = 1;

    // --- Singleton management ---

    private static final Scheduler INSTANCE = new Scheduler();
    public static Scheduler get() {
        return INSTANCE;
    }

    // --- Node type definitions ---

    /*
     * Example Schedule Tree:
     * Only subsystems can have children
     *
     * Drive [subsystem]
     *  |- TalonMotor [subsystem]
     *  |- TalonMotor [subsystem]
     *  |- ...
     * PathFollower [subsystem]
     *  |- DriveToPointCommand [command]
     */

    private static abstract class ScheduleNode {
        // Unique identifier for Messenger API
        public final UUID id = UUID.randomUUID();

        public SubsystemNode parent;

        public abstract void periodicState(RobotState state);
        public abstract void remove(Scheduler sch);
    }

    private static final class SubsystemNode extends ScheduleNode {
        private final Subsystem subsystem;
        private final List<ScheduleNode> children;

        public SubsystemNode(Subsystem subsystem) {
            this.subsystem = subsystem;
            children = new ArrayList<>();
        }

        public void initState(RobotState state) {
            switch (state) {
                case DISABLED:   subsystem.disabledInit();   break;
                case AUTONOMOUS: subsystem.autonomousInit(); break;
                case TELEOP:     subsystem.teleopInit();     break;
                case TEST:       subsystem.testInit();       break;
            }

            for (ScheduleNode node : new ArrayList<>(children)) {
                if (node instanceof SubsystemNode) {
                    ((SubsystemNode) node).initState(state);
                }
            }
        }

        @Override
        public void periodicState(RobotState state) {
            subsystem.periodic();

            switch (state) {
                case DISABLED:   subsystem.disabledPeriodic();   break;
                case AUTONOMOUS: subsystem.autonomousPeriodic(); break;
                case TELEOP:     subsystem.teleopPeriodic();     break;
                case TEST:       subsystem.testPeriodic();       break;
            }

            for (ScheduleNode node : new ArrayList<>(children)) {
                Profiler.push(node.toString());
                node.periodicState(state);
                Profiler.pop();
            }
        }

        @Override
        public void remove(Scheduler sch) {
            sch.removeSubsystem(subsystem);
        }

        @Override
        public String toString() {
            return subsystem.getClass().getSimpleName() + " " + id;
        }
    }

    private static final class CommandNode extends ScheduleNode {
        private final Command command;

        public CommandNode(Command command) {
            this.command = command;
        }

        @Override
        public void periodicState(RobotState state) {
            boolean finished = command.run();
            if (finished) {
                System.out.println("Command finished: " + command);
                INSTANCE.removeCommand(command);
            }
        }

        @Override
        public void remove(Scheduler sch) {
            sch.removeCommand(command);
        }

        @Override
        public String toString() {
            return command.getClass().getSimpleName() + " " + id;
        }
    }

    private final List<SubsystemNode> rootSubsystems;
    private final Map<Subsystem, SubsystemNode> subsystemNodes;

    private final List<CommandNode> rootCommands;
    private final Map<Command, CommandNode> commandNodes;

    private final Map<Subsystem, Set<ScheduleNode>> unsatisfiedParentLinks;

    private MessengerClient msg;

    public Scheduler() {
        rootSubsystems = new ArrayList<>();
        subsystemNodes = new IdentityHashMap<>();
        unsatisfiedParentLinks = new IdentityHashMap<>();

        rootCommands = new ArrayList<>();
        commandNodes = new IdentityHashMap<>();
    }

    // --- Node management functions ---

    private void linkNodes(Subsystem parent, ScheduleNode node) {
        SubsystemNode parentNode = subsystemNodes.get(parent);
        if (parentNode != null) {
            parentNode.children.add(node);
            node.parent = parentNode;
        } else {
            unsatisfiedParentLinks.computeIfAbsent(parent, (p) -> new HashSet<>())
                    .add(node);
        }
    }

    // Adding a subsystem or command as a child of a subsystem links
    // it with the parent subsystem. This means that when the parent
    // is removed, all of its children will also be removed.

    public void addSubsystem(Subsystem s) { addSubsystem(null, s); }
    public void addSubsystem(Subsystem parent, Subsystem ss) {
        SubsystemNode node = new SubsystemNode(ss);

        if (parent != null) {
            linkNodes(parent, node);
        } else {
            rootSubsystems.add(node);
        }

        subsystemNodes.put(ss, node);
        if (unsatisfiedParentLinks.containsKey(ss)) {
            for (ScheduleNode child : unsatisfiedParentLinks.remove(ss)) {
                node.children.add(child);
                child.parent = node;
            }
        }
    }

    public void removeSubsystem(Subsystem s) {
        SubsystemNode node = subsystemNodes.get(s);
        if (node == null)
            return;
        for (ScheduleNode child : node.children) {
            child.remove(this);
        }
        subsystemNodes.remove(s);
        if (node.parent != null)
            node.parent.children.remove(node);

        rootSubsystems.remove(node);
    }

    public void addCommand(Command cmd) { addCommand(null, cmd); }
    public void addCommand(Subsystem parent, Command cmd) {
        CommandNode node = new CommandNode(cmd);

        if (parent != null) {
            linkNodes(parent, node);
        } else {
            rootCommands.add(node);
        }

        commandNodes.put(cmd, node);
    }

    public void removeCommand(Command cmd) {
        CommandNode node = commandNodes.remove(cmd);
        if (node != null && node.parent != null)
            node.parent.children.remove(node);
        rootCommands.remove(node);
    }

    // --- Main functions ---

    public void initState(RobotState state) {
        for (SubsystemNode node : new ArrayList<>(rootSubsystems)) {
            node.initState(state);
        }
    }

    public void periodicState(RobotState state) {
        for (CommandNode cmd : new ArrayList<>(rootCommands)) {
            Profiler.push(cmd.toString());
            cmd.periodicState(state);
            Profiler.pop();
        }

        for (SubsystemNode node : new ArrayList<>(rootSubsystems)) {
            Profiler.push(node.toString());
            node.periodicState(state);
            Profiler.pop();
        }
    }

    // --- Messenger API definitions ---

    public void initMessenger(MessengerClient msg) {
        this.msg = msg;
        msg.addHandler(MSG_QUERY, this::handleQuery);
    }

    private void addUUID(MessageBuilder builder, UUID uuid) {
        builder.addLong(uuid.getLeastSignificantBits());
        builder.addLong(uuid.getMostSignificantBits());
    }

    private void handleQuery(String type, MessageReader reader) {
        MessageBuilder builder = msg.prepare(MSG_QUERY_RESPONSE);

        // Commands and subsystems are combined into one data array
        builder.addInt(rootCommands.size() + rootSubsystems.size());

        for (CommandNode cmd : rootCommands) {
            writeCommandNode(builder, cmd);
        }

        for (SubsystemNode ss : rootSubsystems) {
            writeSubsystemNode(builder, ss);
        }

        builder.send();
    }

    private String getTypeName(Object obj) {
        return obj.getClass().getSimpleName();
    }

    private void writeSubsystemNode(MessageBuilder builder, SubsystemNode node) {
        addUUID(builder, node.id);
        builder.addString(getTypeName(node.subsystem));
        builder.addByte(TYPE_CODE_SUBSYSTEM);

        builder.addInt(node.children.size());
        for (ScheduleNode child : node.children) {
            writeScheduleNode(builder, child);
        }
    }

    private void writeCommandNode(MessageBuilder builder, CommandNode node) {
        addUUID(builder, node.id);
        builder.addString(getTypeName(node.command));
        builder.addByte(TYPE_CODE_COMMAND);

        // TODO-Ryan: Interval
    }

    private void writeScheduleNode(MessageBuilder builder, ScheduleNode node) {
        if (node instanceof SubsystemNode)
            writeSubsystemNode(builder, (SubsystemNode) node);
        else
            writeCommandNode(builder, (CommandNode) node);
    }
}
