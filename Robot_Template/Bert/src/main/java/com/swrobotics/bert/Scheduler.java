package com.swrobotics.bert;

import com.swrobotics.bert.commands.Command;
import com.swrobotics.bert.profiler.Profiler;
import com.swrobotics.bert.subsystems.Subsystem;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Scheduler {
    private static final Scheduler INSTANCE = new Scheduler();
    public static Scheduler get() {
        return INSTANCE;
    }

    private final Queue<Subsystem> subsystems;
    private final Queue<CommandTimer> commands;

    private final Set<Subsystem> subsystemsToRemove;

    private Scheduler() {
        subsystems = new ConcurrentLinkedQueue<>();
        commands = new ConcurrentLinkedQueue<>();

        subsystemsToRemove = new HashSet<>();
    }

    public void addSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    public boolean hasSubsystem(Subsystem subsystem) {
        return subsystems.contains(subsystem);
    }

    public void removeSubsystem(Subsystem subsystem) {
        subsystemsToRemove.add(subsystem);
    }

    public void addCommand(Command command) {
        commands.add(new CommandTimer(command));
        command.init();
    }

    public void cancelCommand(Command command) {
        CommandTimer toRemove = null;
        for (CommandTimer timer : commands) {
            if (timer.command.equals(command)) {
                toRemove = timer;
            }
        }

        if (toRemove != null) {
            commands.remove(toRemove);
        }
    }

    public boolean isCommandRunning(Command command) {
        for (CommandTimer timer : commands) {
            if (timer.command.equals(command)) {
                return true;
            }
        }

        return false;
    }

    private static class CommandTimer {
        private final Command command;
        private int timer;

        public CommandTimer(Command command) {
            this.command = command;
            timer = command.getInterval();
        }

        public boolean update() {
            timer--;
            if (timer <= 0) {
                timer = command.getInterval();

                return command.run();
            }

            return false;
        }
    }

    private void updateCommands() {
        subsystems.removeAll(subsystemsToRemove);
        subsystemsToRemove.clear();

        for (Iterator<CommandTimer> iterator = commands.iterator(); iterator.hasNext();) {
            CommandTimer timer = iterator.next();
            Profiler.get().push(timer.command.getClass().getSimpleName());

            if (timer.update()) {
                iterator.remove();
                timer.command.end();
            }

            Profiler.get().pop();
        }
    }

    public void robotInit() {
        for (Subsystem system : subsystems) {
            system.robotInit();
        }
    }

    public void robotPeriodic() {
        for (Subsystem system : subsystems) {
            Profiler.get().push(system.getClass().getSimpleName());
            system.robotPeriodic();
            Profiler.get().pop();
        }

        updateCommands();
    }

    public void disabledInit() {
        for (Subsystem system : subsystems) {
            system.disabledInit();
        }
    }

    public void disabledPeriodic() {
        for (Subsystem system : subsystems) {
            Profiler.get().push(system.getClass().getSimpleName());
            system.disabledPeriodic();
            Profiler.get().pop();
        }
    }

    public void teleopInit() {
        for (Subsystem system : subsystems) {
            system.teleopInit();
        }
    }

    public void teleopPeriodic() {
        for (Subsystem system : subsystems) {
            Profiler.get().push(system.getClass().getSimpleName());
            system.teleopPeriodic();
            Profiler.get().pop();
        }
    }

    public void autonomousInit() {
        for (Subsystem system : subsystems) {
            system.autonomousInit();
        }
    }

    public void autonomousPeriodic() {
        for (Subsystem system : subsystems) {
            Profiler.get().push(system.getClass().getSimpleName());
            system.autonomousPeriodic();
            Profiler.get().pop();
        }
    }

    public void testInit() {
        for (Subsystem system : subsystems) {
            system.testInit();
        }
    }

    public void testPeriodic() {
        for (Subsystem system : subsystems) {
            Profiler.get().push(system.getClass().getSimpleName());
            system.testPeriodic();
            Profiler.get().pop();
        }
    }
}
