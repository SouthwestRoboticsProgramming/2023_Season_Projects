package com.team2129.lib.routine;

import java.util.Arrays;
import java.util.EnumSet;

import com.team2129.lib.wpilib.RobotState;

// TODO: Add way to remove from scheduler & docs
// TODO: End routine from implementation, implement run states
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
        Scheduler.get().addRoutine(this);
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
