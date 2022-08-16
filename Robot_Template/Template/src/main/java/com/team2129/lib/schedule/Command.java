package com.team2129.lib.schedule;

import com.team2129.lib.time.Duration;
import com.team2129.lib.time.TimeUnit;
import com.team2129.lib.wpilib.AbstractRobot;

/**
 * An action that runs until it is complete.
 * A command must be scheduled through the sheduler.
 */
public interface Command {
    Duration DEFAULT_INTERVAL = new Duration(1 / AbstractRobot.get().getPeriodicPerSecond(), TimeUnit.SECONDS);

    default void init() {}

    /**
     * This function runs every periodic until it returns true.
     * @return Returns true when command is compete.
     */
    boolean run();

    /**
     * End the command prematurely.
     * @param wasCancelled
     */
    default void end(boolean wasCancelled) {}

    default Duration getInterval() {
        return DEFAULT_INTERVAL;
    }
}
