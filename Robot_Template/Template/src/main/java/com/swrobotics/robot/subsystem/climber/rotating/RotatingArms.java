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
        leftArm = new RotatingArm(LEFT_ARM_ID, "Left");
        rightArm = new RotatingArm(RIGHT_ARM_ID, "Right");

        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, leftArm);
        sch.addSubsystem(this, rightArm);

        targetAngle = Angle.zero();
    }

    public boolean leftInTolerance() {
        return leftArm.inTolerance();
    }

    public boolean rightInTolerance() {
        return rightArm.inTolerance();
    }

    public void setTargetAngle(Angle target) {
        targetAngle = target;
    }

    @Override
    public void periodic() {
        leftArm.setTargetAngle(targetAngle);
        rightArm.setTargetAngle(targetAngle);
    }

    public RotatingArm getLeft() {
        return leftArm;
    }

    public RotatingArm getRight() {
        return rightArm;
    }
}
