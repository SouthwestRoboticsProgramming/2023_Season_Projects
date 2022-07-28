package com.team2129.lib.routine;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.wpilib.AbstractRobot;
import com.team2129.lib.wpilib.RobotState;

public final class Scheduler {
    private static final String MSG_IN_GET_ROUTINES = "Scheduler:GetRoutines";
    private static final String MSG_OUT_ROUTINES = "Scheduler:Routines";
    private static final String MSG_OUT_ADD_ROUTINE = "Scheduler:AddRoutine";
    private static final String MSG_OUT_REMOVE_ROUTINE = "Scheduler:RemoveRoutine";

    private static final Scheduler INSTANCE = new Scheduler();
    public static Scheduler get() {
        return INSTANCE;
    }

    private final List<Routine> routines;

    // Used to uniquely identify Routines over Messenger
    private final Map<Routine, UUID> identifiers;

    // Prevents ConcurrentModificationException if Routines are added or
    // removed from within their init(), end(), and periodic() functions
    private final List<Runnable> deferredChanges;
    private boolean shouldDeferChanges;

    public Scheduler() {
        routines = new ArrayList<>();
        identifiers = new IdentityHashMap<>();
        deferredChanges = new ArrayList<>();
        shouldDeferChanges = false;
    }

    public void reportAddRoutine(Routine parent, Routine routine) {
        MessengerClient msg = AbstractRobot.get().getMessenger();
        if (msg != null) {
            UUID pid = parent == null ? new UUID(0, 0) : identifiers.computeIfAbsent(parent, (r) -> UUID.randomUUID());
            UUID id = identifiers.computeIfAbsent(routine, (r) -> UUID.randomUUID());
            msg.prepare(MSG_OUT_ADD_ROUTINE)
                    .addLong(pid.getLeastSignificantBits())
                    .addLong(pid.getMostSignificantBits())
                    .addLong(id.getLeastSignificantBits())
                    .addLong(id.getMostSignificantBits())
                    .addString(routine.getClass().getSimpleName())
                    .send();
        }
    }

    public void reportRemoveRoutine(Routine parent, Routine routine) {
        if (!identifiers.containsKey(routine))
            return;

        MessengerClient msg = AbstractRobot.get().getMessenger();
        if (msg != null) {
            UUID pid = parent == null ? new UUID(0, 0) : identifiers.computeIfAbsent(parent, (r) -> UUID.randomUUID());
            UUID id = identifiers.computeIfAbsent(routine, (r) -> UUID.randomUUID());
            msg.prepare(MSG_OUT_REMOVE_ROUTINE)
                    .addLong(pid.getLeastSignificantBits())
                    .addLong(pid.getMostSignificantBits())
                    .addLong(id.getLeastSignificantBits())
                    .addLong(id.getMostSignificantBits())
                    .send();
        }
    }

    public void addRoutine(Routine routine) {
        if (shouldDeferChanges) {
            deferredChanges.add(() -> routines.add(routine));
        } else {
            routines.add(routine);
        }

        reportAddRoutine(null, routine);
    }

    public void removeRoutine(Routine routine) {
        if (shouldDeferChanges) {
            deferredChanges.add(() -> routines.remove(routine));
        } else {
            routines.remove(routine);
        }

        reportRemoveRoutine(null, routine);
    }

    public void onStateSwitch(RobotState state) {
        beginCoModProtection();
        for (Routine r : routines) {
            r.onStateChange(state);
        }
        endCoModProtection();
    }

    public void periodic() {
        beginCoModProtection();
        for (Routine r : routines) {
            r.doPeriodic();
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

    public void registerMessengerQueryHook(MessengerClient msg) {
        msg.addHandler(MSG_IN_GET_ROUTINES, (type, reader) -> {
            MessageBuilder b = msg.prepare(MSG_OUT_ROUTINES);
            b.addInt(routines.size());
            for (Routine r : routines) {
                UUID id = identifiers.computeIfAbsent(r, (u) -> UUID.randomUUID());
                b.addLong(id.getLeastSignificantBits());
                b.addLong(id.getMostSignificantBits());
                b.addString(r.getClass().getSimpleName());
            }
            b.send();
        });
    }
}
