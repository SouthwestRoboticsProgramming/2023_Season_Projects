package com.swrobotics.robot.subsystem.climber;

import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.robot.subsystem.climber.rotating.RotatingArms;
import com.swrobotics.robot.subsystem.climber.telescoping.TelescopingArms;

public class Climber implements Subsystem {

    private final RotatingArms rotating;
    private final TelescopingArms telescoping;

    public Climber() {
        rotating = new RotatingArms();
        telescoping = new TelescopingArms();

        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, rotating);
        sch.addSubsystem(this, telescoping);
    }
    

    public void setTargetState(ClimberState state) {
        rotating.setTargetAngle(state.getRotAngle());
        telescoping.setTargetHeight(state.getTelePercent(), state.getLoaded());
    }

    public boolean inTolerance() {
        boolean leftRot = rotating.leftInTolerance();
        boolean rightRot = rotating.rightInTolerance();
        boolean leftTele = telescoping.leftInTolerance();
        boolean rightTele = telescoping.rightInTolerance();

        boolean tol = leftRot && rightRot && leftTele && rightTele;
        if (tol) {
            System.out.println("In tolerance");

            return true;
        }

        String out = "Out of tolerance: ";
        if (!leftRot) out += "LeftRot " + rotating.getLeft().getTolInfo() + " ";
        if (!rightRot) out += "RightRot " + rotating.getRight().getTolInfo() + " ";
        if (!leftTele) out += "LeftTele " + telescoping.getLeft().getTolInfo() + " ";
        if (!rightTele) out += "RightTele " + telescoping.getRight().getTolInfo();

        System.out.println(out);
        return false;
    }
}
