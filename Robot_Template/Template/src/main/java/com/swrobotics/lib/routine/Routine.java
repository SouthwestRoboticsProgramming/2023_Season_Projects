package com.swrobotics.lib.routine;

import java.util.Arrays;
import java.util.EnumSet;

import com.swrobotics.lib.RobotState;

// TODO: Add way to remove from scheduler & docs
public abstract class Routine {
    private final EnumSet<RobotState> runStates;
    private boolean running = true;

    // Run in all states (default)
    public Routine() {
        runStates = EnumSet.allOf(RobotState.class);
        Scheduler.get().addRoutine(this);
    }

    // Run in specified states
    public Routine(RobotState... runStates) {
        this.runStates = EnumSet.noneOf(RobotState.class);
        this.runStates.addAll(Arrays.asList(runStates));
    }

    public boolean runsInState(RobotState state) {
        return runStates.contains(state);
    }

    // Implementation is optional
    protected void init() {}
    protected void end() {}
    protected void periodic() {}
    protected void deconstruct() {}
}
