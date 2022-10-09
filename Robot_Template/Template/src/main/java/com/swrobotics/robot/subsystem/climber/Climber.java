package com.swrobotics.robot.subsystem.climber;

import com.swrobotics.robot.subsystem.climber.rotating.RotatingArms;
import com.swrobotics.robot.subsystem.climber.telescoping.TelescopingArms;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

public class Climber implements Subsystem {

    // private final RotatingArms rotating;
    private final TelescopingArms telescoping;

    public Climber() {
        // rotating = new RotatingArms();
        telescoping = new TelescopingArms();

        Scheduler sch = Scheduler.get();
        // sch.addSubsystem(this, rotating);
        sch.addSubsystem(this, telescoping);
    }
    

    public void setTargetState(ClimberState state) {
        // rotating.setTargetAngle(state.getRotAngle());
        telescoping.setTargetHeight(state.getTelePercent(), state.getLoaded());
    }

    public boolean inTolerance() {
        // return rotating.inTolerance() && telescoping.inTolerance();
        return telescoping.inTolerance();
    }
}
