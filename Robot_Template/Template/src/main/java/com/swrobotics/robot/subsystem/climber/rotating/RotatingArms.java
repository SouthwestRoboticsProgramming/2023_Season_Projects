package com.swrobotics.robot.subsystem.climber.rotating;

import com.team2129.lib.math.Angle;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

public class RotatingArms implements Subsystem {

    private static final int LEFT_ARM_ID = 10;
    private static final int RIGHT_ARM_ID = 11;

    private final RotatingArm leftArm;
    private final RotatingArm rightArm;

    private Angle targetAngle;

    public RotatingArms() {
        leftArm = new RotatingArm(LEFT_ARM_ID);
        rightArm = new RotatingArm(RIGHT_ARM_ID);

        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, leftArm);
        sch.addSubsystem(this, rightArm);
    }

    public boolean inTolerance() {
        return leftArm.inTolerance() && rightArm.inTolerance();
    }

    public void setTargetAngle(Angle target) {
        targetAngle = target;
    }

    @Override
    public void periodic() {
        leftArm.setTargetAngle(targetAngle);
        rightArm.setTargetAngle(targetAngle);
    }
}
