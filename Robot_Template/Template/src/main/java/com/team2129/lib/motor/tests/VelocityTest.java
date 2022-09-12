package com.team2129.lib.motor.tests;

import com.team2129.lib.schedule.TestCommand;

public class VelocityTest extends TestCommand {

    public VelocityTest(double timeoutSeconds) {
        setTimeout(timeoutSeconds);
    }

    @Override
    public boolean run() {
        System.out.println(getTime());
        return hasTimedOut();
    }

    @Override
    public boolean hasFailed() {
        return hasTimedOut() && /*Not at setpoint*/ true;
    }
    
}
