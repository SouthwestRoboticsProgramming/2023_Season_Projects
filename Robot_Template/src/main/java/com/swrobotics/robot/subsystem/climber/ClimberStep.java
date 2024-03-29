package com.swrobotics.robot.subsystem.climber;

import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Command;

public class ClimberStep implements Command {

    private final Climber climber;
    private final ClimberState state;

    public ClimberStep(Climber climber, NTDouble telePercent, NTDouble rotAngle, boolean loaded) {
        this.climber = climber;
        state = new ClimberState(telePercent, rotAngle, loaded);
    }

    @Override
    public boolean run() {
        climber.setTargetState(state);
        // return climber.inTolerance();
        return false;
    }
    
}
