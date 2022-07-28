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
    }

    // Run in specified states
    public Routine(RobotState... runStates) {
        this.runStates = EnumSet.noneOf(RobotState.class);
        this.runStates.addAll(Arrays.asList(runStates));
    }

    public final boolean runsInState(RobotState state) {
        return runStates.contains(state);
    }

    // Implementation is optional
    protected void init() {}
    protected void end() {}
    protected void periodic() {}

    // This should be called by implementations to indicate that they
    // are done running.
    protected final void finish() {
        cancel(true);
    }

    public final void cancel(boolean removeFromScheduler) {
        running = false;
        end();

        // We don't need to check if this routine is actually in the scheduler
        // because removing a routine that is not present does nothing
        if (removeFromScheduler)
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

    public final boolean isRunning() {
        return running;
    }
}
