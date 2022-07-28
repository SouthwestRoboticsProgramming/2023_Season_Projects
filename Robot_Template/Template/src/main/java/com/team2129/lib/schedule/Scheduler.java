package com.team2129.lib.schedule;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.time.Repeater;
import com.team2129.lib.wpilib.RobotState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Scheduler {
    private static final String MSG_IN_QUERY = "Scheduler:Query";
    private static final String MSG_OUT_QUERY_RESPONSE = "Scheduler:QueryResponse";
    private static final String MSG_OUT_SUBSYSTEM_ADDED = "Scheduler:SubsystemAdded";
    private static final String MSG_OUT_SUBSYSTEM_REMOVED = "Scheduler:SubsystemRemoved";
    private static final String MSG_OUT_COMMAND_ADDED = "Scheduler:CommandAdded";
    private static final String MSG_OUT_COMMAND_REMOVED = "Scheduler:CommandRemoved";

    private static final Scheduler INSTANCE = new Scheduler();
    public static Scheduler get() {
        return INSTANCE;
    }

    private static final class SubsystemWrapper {
        Subsystem subsystem;
        UUID id;

        SubsystemWrapper parent;
        List<SubsystemWrapper> children = new ArrayList<>();
        boolean childrenLocked = false; // To prevent ConcurrentModificationException

        public SubsystemWrapper(SubsystemWrapper parent, Subsystem subsystem) {
            this.parent = parent;
            this.subsystem = subsystem;
            id = UUID.randomUUID();
        }

        void init(RobotState state) {
            switch (state) {
                case DISABLED:   subsystem.disabledInit();   break;
                case AUTONOMOUS: subsystem.autonomousInit(); break;
                case TELEOP:     subsystem.teleopInit();     break;
                case TEST:       subsystem.testInit();       break;
            }
        }

        void periodic(RobotState state) {
            subsystem.periodic();
            switch (state) {
                case DISABLED:   subsystem.disabledPeriodic();   break;
                case AUTONOMOUS: subsystem.autonomousPeriodic(); break;
                case TELEOP:     subsystem.teleopPeriodic();     break;
                case TEST:       subsystem.testPeriodic();       break;
            }
        }
    }

    private static final class CommandWrapper {
        Command command;
        UUID id;
        Repeater repeater;

        boolean done = false;

        public CommandWrapper(Command command) {
            this.command = command;
            id = UUID.randomUUID();
            repeater = new Repeater(command.getInterval(), () -> done = command.run());
        }

        public boolean run() {
            repeater.tick();
            return done;
        }
    }

    private final List<SubsystemWrapper> subsystems;
    private final List<CommandWrapper> commands;

    // Prevents ConcurrentModificationException
    private final List<Runnable> deferredChanges;
    private boolean shouldDeferChanges;

    private MessengerClient msg;

    public Scheduler() {
        subsystems = new ArrayList<>();
        commands = new ArrayList<>();

        deferredChanges = new ArrayList<>();
        shouldDeferChanges = false;
    }

    private void addUUID(MessageBuilder builder, UUID id) {
        builder.addLong(id.getLeastSignificantBits());
        builder.addLong(id.getMostSignificantBits());
    }

    private void describeCommand(MessageBuilder builder, CommandWrapper c) {
        addUUID(builder, c.id);
        builder.addString(c.command.getClass().getSimpleName());
        builder.addDouble(c.command.getInterval().getDurationNanos());
    }

    private void describeSubsystem(MessageBuilder builder, SubsystemWrapper s) {
        addUUID(builder, s.id);
        builder.addString(s.subsystem.getClass().getSimpleName());
    }

    public void registerMessengerQueryHook(MessengerClient msg) {
        this.msg = msg;

        msg.addHandler(MSG_IN_QUERY, (type, reader) -> {
            MessageBuilder builder = msg.prepare(MSG_OUT_QUERY_RESPONSE);

            builder.addInt(subsystems.size());
            for (SubsystemWrapper s : subsystems) {
                describeSubsystem(builder, s);

                // Children only need to be sent once
                builder.addInt(s.children.size());
                for (SubsystemWrapper child : s.children) {
                    addUUID(builder, child.id);
                }
            }

            builder.addInt(commands.size());
            for (CommandWrapper c : commands) {
                describeCommand(builder, c);
            }

            builder.send();
        });
    }

    private void reportSubsystemAdd(SubsystemWrapper s) {
        if (msg == null) return;

        MessageBuilder builder = msg.prepare(MSG_OUT_SUBSYSTEM_ADDED);

        // Specify parent so receiver can link it
        if (s.parent == null)
            builder.addLong(0).addLong(0);
        else
            addUUID(builder, s.parent.id);

        describeSubsystem(builder, s);
        builder.send();
    }

    private void reportSubsystemRemove(SubsystemWrapper s) {
        if (msg == null) return;

        MessageBuilder builder = msg.prepare(MSG_OUT_SUBSYSTEM_REMOVED);
        addUUID(builder, s.id);
        builder.send();
    }

    private void reportCommandAdd(CommandWrapper c) {
        if (msg == null) return;

        MessageBuilder builder = msg.prepare(MSG_OUT_COMMAND_ADDED);
        describeCommand(builder, c);
        builder.send();
    }

    private void reportCommandRemove(CommandWrapper c) {
        if (msg == null) return;

        MessageBuilder builder = msg.prepare(MSG_OUT_COMMAND_REMOVED);
        addUUID(builder, c.id);
        builder.send();
    }

    private SubsystemWrapper lookUpSubsystemWrapper(Subsystem s) {
        SubsystemWrapper wrapper = null;
        for (SubsystemWrapper w : subsystems) {
            if (w.subsystem == s) {
                wrapper = w;
            }
        }
        return wrapper;
    }

    private CommandWrapper lookUpCommandWrapper(Command cmd) {
        CommandWrapper wrapper = null;
        for (CommandWrapper w : commands) {
            if (w.command == cmd) {
                wrapper = w;
            }
        }
        return wrapper;
    }

    public void addSubsystem(Subsystem s) {
        addSubsystem(null, s);
    }

    /**
     * Adds a subsystem to the scheduler and attaches it to a parent
     * subsystem. This link means that when the parent subsystem is
     * removed, the specified subsystem will be removed as well.
     *
     * @param parent parent subsystem
     * @param s subsystem to add
     */
    public void addSubsystem(Subsystem parent, Subsystem s) {
        s.onAdd();

        SubsystemWrapper wrapper = new SubsystemWrapper(lookUpSubsystemWrapper(parent), s);
        if (shouldDeferChanges)
            deferredChanges.add(() -> subsystems.add(wrapper));
        else
            subsystems.add(wrapper);

        reportSubsystemAdd(wrapper);
    }

    private void removeSubsystem(SubsystemWrapper wrapper) {
        wrapper.subsystem.onRemove();

        if (wrapper.parent != null && !wrapper.parent.childrenLocked)
            wrapper.parent.children.remove(wrapper);

        wrapper.childrenLocked = true;
        for (SubsystemWrapper child : wrapper.children) {
            removeSubsystem(child);
        }
        wrapper.childrenLocked = false;

        if (shouldDeferChanges) {
            final SubsystemWrapper finalWrapper = wrapper;
            deferredChanges.add(() -> subsystems.remove(finalWrapper));
        } else {
            subsystems.remove(wrapper);
        }

        reportSubsystemRemove(wrapper);
    }

    /**
     * Removes a subsystem and its children from the scheduler.
     *
     * @param s subsystem to remove
     */
    public void removeSubsystem(Subsystem s) {
        removeSubsystem(lookUpSubsystemWrapper(s));
    }

    public void addCommand(Command cmd) {
        cmd.init();

        CommandWrapper wrapper = new CommandWrapper(cmd);

        if (shouldDeferChanges)
            deferredChanges.add(() -> commands.add(wrapper));
        else
            commands.add(wrapper);

        reportCommandAdd(wrapper);
    }

    private void removeCommand(CommandWrapper wrapper) {
        if (commands.contains(wrapper))
            wrapper.command.end(true);

        if (shouldDeferChanges)
            deferredChanges.add(() -> commands.remove(wrapper));
        else
            commands.remove(wrapper);

        reportCommandRemove(wrapper);
    }

    public void removeCommand(Command cmd) {
        removeCommand(lookUpCommandWrapper(cmd));
    }

    public void initState(RobotState state) {
        beginCoModProtection();
        for (SubsystemWrapper subsystem : subsystems) {
            subsystem.init(state);
        }
        endCoModProtection();
    }

    public void periodicState(RobotState state) {
        List<CommandWrapper> endedCommands = new ArrayList<>();
        beginCoModProtection();
        for (CommandWrapper cmd : commands) {
            if (cmd.run()) {
                endedCommands.add(cmd);
            }
        }
        for (CommandWrapper ended : endedCommands) {
            ended.command.end(false);
            commands.remove(ended);
        }
        endCoModProtection();

        beginCoModProtection();
        for (SubsystemWrapper subsystem : subsystems) {
            subsystem.periodic(state);
        }
        endCoModProtection();
    }

    private void beginCoModProtection() {
        shouldDeferChanges = true;
        deferredChanges.clear();
    }

    private void endCoModProtection() {
        shouldDeferChanges = false;

        // Apply all changes that happened within the protection block
        for (Runnable change : deferredChanges) {
            change.run();
        }
    }
}
