package com.swrobotics.robot.test;

public final class ExampleTest extends Test {
    private final boolean result;

    public ExampleTest(boolean result) {
        this.result = result;
    }

    @Override
    protected void periodic() {
        // Report the result of the test, true is success, false is failure
        reportResult(result);
    }
}
