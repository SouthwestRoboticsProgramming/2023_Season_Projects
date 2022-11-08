package com.swrobotics.robot.test;

import com.swrobotics.lib.schedule.CommandSequence;

public final class TestSequence extends CommandSequence {
    public TestSequence() {
        append(new ExampleTest(true)); // Passing test
        append(new ExampleTest(false)); // Failing test
    }
}
