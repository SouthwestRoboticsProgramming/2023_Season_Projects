package com.swrobotics.lib.routine;

import java.util.Arrays;
import java.util.EnumSet;

import com.swrobotics.lib.RobotState;

public abstract class Routine {
    private final EnumSet<RobotState> runStates;
    private boolean running = true;

    // Run in all states (default)
    public Routine() {
        runStates = EnumSet.allOf(RobotState.class);
    }

    // Run in specified states
    public Routine(RobotState... runStates) {
        this.runStates = EnumSet.noneOf(RobotState.class);
        this.runStates.addAll(Arrays.asList(runStates));
    }

    public boolean runsInState(RobotState state) {
        return runStates.contains(state);
    }

    protected abstract void onInit();
    protected abstract void onEnd();
    protected abstract void onPeriodic();
    protected void onCancel() {} // Implementation is optional

    protected void finish() {
        running = false;
        onEnd();
    }

    public void cancel() {
        running = false;
        onCancel();
        onEnd();
    }
}
