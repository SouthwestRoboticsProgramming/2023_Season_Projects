package com.team2129.lib.routine;

import java.util.Arrays;
import java.util.EnumSet;

import com.team2129.lib.wpilib.RobotState;

// TODO: Add way to remove from scheduler & docs
// TODO: End routine from implementation, implement run states
public abstract class Routine {
    private final EnumSet<RobotState> runStates;
    private boolean running = false;
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

    public final void cancel(boolean canRestartOnStateChange) {
        running = false;
        end();

        // If this routine will never start again, the
        // scheduler no longer needs to keep track of it.
        // If this routine can restart, it stays in the
        // scheduler, but periodic is not called.
        if (!canRestartOnStateChange)
            Scheduler.get().removeRoutine(this);
    }

    public final void onStateChange(RobotState newState) {
        boolean newRunning = runsInState(newState);
        if (running && !newRunning)
            end();
        if (!running && newRunning)
            init();
        running = newRunning;
    }

    public final void doPeriodic() {
        if (running) {
            periodic();
        }
    }
}
