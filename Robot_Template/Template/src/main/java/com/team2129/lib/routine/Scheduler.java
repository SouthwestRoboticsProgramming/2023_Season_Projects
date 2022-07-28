package com.team2129.lib.routine;

import java.util.ArrayList;
import java.util.List;

import com.team2129.lib.wpilib.RobotState;

public final class Scheduler {
    private static final Scheduler INSTANCE = new Scheduler();
    public static Scheduler get() {
        return INSTANCE;
    }

    private final List<Routine> routines;

    // Prevents ConcurrentModificationException if Routines are added or
    // removed from within their init(), end(), and periodic() functions
    private final List<Runnable> deferredChanges;
    private boolean shouldDeferChanges;

    public Scheduler() {
        routines = new ArrayList<>();
        deferredChanges = new ArrayList<>();
        shouldDeferChanges = false;
    }

    public void addRoutine(Routine routine) {
        if (shouldDeferChanges) {
            deferredChanges.add(() -> routines.add(routine));
        } else {
            routines.add(routine);
        }
    }

    public void removeRoutine(Routine routine) {
        if (shouldDeferChanges) {
            deferredChanges.add(() -> routines.remove(routine));
        } else {
            routines.remove(routine);
        }
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
}
