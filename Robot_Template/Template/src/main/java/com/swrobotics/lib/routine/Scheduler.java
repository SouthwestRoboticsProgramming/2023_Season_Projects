package com.swrobotics.lib.routine;

import com.swrobotics.lib.RobotState;

import java.util.ArrayList;
import java.util.List;

public final class Scheduler {
    private static final Scheduler INSTANCE = new Scheduler();

    public static Scheduler get() {
        return INSTANCE;
    }

    private final List<Routine> routines;
    private RobotState currentState;

    public Scheduler() {
        routines = new ArrayList<>();
    }

    public void addRoutine(Routine routine) {
        routines.add(routine);
    }

    public void onStateSwitch(RobotState state) {
        for (Routine r : routines) {
            if (!r.runsInState(currentState) && r.runsInState(state)) {
                r.init();
            }
            if (r.runsInState(currentState) && !r.runsInState(state)) {
                r.end();
            }
        }

        currentState = state;
    }

    public void periodic() {
        for (Routine r : routines) {
            if (r.runsInState(currentState)) {
                r.periodic();
            }
        }
    }
}
