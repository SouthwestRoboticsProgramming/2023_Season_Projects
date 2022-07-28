package com.swrobotics.robot.subsystem;

import com.team2129.lib.schedule.subsystem.Subsystem;

public final class TestSubsystem implements Subsystem {
    @Override
    public void onAdd() {
        System.out.println("onAdd()");
    }

    @Override
    public void onRemove() {
        System.out.println("onRemove()");
    }

    @Override
    public void periodic() {
        System.out.println("periodic()");
    }

    @Override
    public void disabledInit() {
        System.out.println("disabledInit()");
    }

    @Override
    public void disabledPeriodic() {
        System.out.println("disabledPeriodic()");
    }

    @Override
    public void autonomousInit() {
        System.out.println("autonomousInit()");
    }

    @Override
    public void autonomousPeriodic() {
        System.out.println("autonomousPeriodic()");
    }

    @Override
    public void teleopInit() {
        System.out.println("teleopInit()");
    }

    @Override
    public void teleopPeriodic() {
        System.out.println("teleopPeriodic()");
    }

    @Override
    public void testInit() {
        System.out.println("testInit()");
    }

    @Override
    public void testPeriodic() {
        System.out.println("testPeriodic()");
    }
}
