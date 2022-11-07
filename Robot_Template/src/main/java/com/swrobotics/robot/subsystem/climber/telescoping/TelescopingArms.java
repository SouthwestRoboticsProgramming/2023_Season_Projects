package com.swrobotics.robot.subsystem.climber.telescoping;

import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.schedule.Subsystem;

public class TelescopingArms implements Subsystem {
    private static final int LEFT_TELE_ID_1 = 6;
    private static final int LEFT_TELE_ID_2 = 7;
    private static final boolean LEFT_TELE_INVERTED = false;
    private static final int RIGHT_TELE_ID_1 = 8;
    private static final int RIGHT_TELE_ID_2 = 9;
    private static final boolean RIGHT_TELE_INVERTED = false;

    private final TelescopingArm leftTele;
    private final TelescopingArm rightTele;

    public TelescopingArms() {
        leftTele = new TelescopingArm(LEFT_TELE_ID_1, LEFT_TELE_ID_2, LEFT_TELE_INVERTED);
        rightTele = new TelescopingArm(RIGHT_TELE_ID_1, RIGHT_TELE_ID_2, RIGHT_TELE_INVERTED);

        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, leftTele);
        sch.addSubsystem(this, rightTele);
    }

    public void setTargetHeight(double percentOfMax, boolean underLoad) {
        leftTele.setHeight(percentOfMax, underLoad);
        rightTele.setHeight(percentOfMax, underLoad);
    }

    public boolean leftInTolerance() {
        return leftTele.inTolerance();
    }

    public boolean rightInTolerance() {
        return rightTele.inTolerance();
    }

    public TelescopingArm getLeft() {
        return leftTele;
    }

    public TelescopingArm getRight() {
        return rightTele;
    }
}
