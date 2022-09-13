package com.team2129.lib.schedule;

import com.team2129.lib.time.Duration;
import com.team2129.lib.time.TimeUnit;
import com.team2129.lib.wpilib.AbstractRobot;

/**
 * An action that runs until it is complete.
 * A command must be scheduled through the scheduler.
 */
public interface Command {
    Duration DEFAULT_INTERVAL = new Duration(1 / AbstractRobot.get().getPeriodicPerSecond(), TimeUnit.SECONDS);

    /**
     * Called before the first time {@code run()} is called,
     * to perform initialization.
     */
    default void init() {}

    /**
     * This function runs every periodic until it returns true.
     * @return Returns true when command is compete.
     */
    boolean run();

    /**
     * Called by the scheduler when the command ends.
     * @param wasCancelled Whether the command was cancelled
     */
    default void end(boolean wasCancelled) {}

    /**
     * Gets the preferred time interval between calls to {@code run()}.
     * @return periodic interval
     */
    default Duration getInterval() {
        return DEFAULT_INTERVAL;
    }
}
