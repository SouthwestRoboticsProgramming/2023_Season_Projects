package com.swrobotics.robot.test;

import com.team2129.lib.schedule.Command;
import edu.wpi.first.wpilibj.DriverStation;

public abstract class Test implements Command {
    private boolean hasResult;

    public Test() {
        hasResult = false;
    }

    protected abstract void periodic();

    protected final void reportResult(boolean success) {
        hasResult = true;

        if (success) {
            System.out.println("Test passed: " + getClass().getSimpleName());
        } else {
            DriverStation.reportError("Test failed: " + getClass().getSimpleName(), true);
        }
    }

    @Override
    public final boolean run() {
        periodic();
        return hasResult;
    }
}
