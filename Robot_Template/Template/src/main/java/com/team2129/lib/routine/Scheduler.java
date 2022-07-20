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
