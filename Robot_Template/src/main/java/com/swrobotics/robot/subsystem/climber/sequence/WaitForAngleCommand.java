package com.swrobotics.robot.subsystem.climber.sequence;

import com.team2129.lib.gyro.Gyroscope;
import com.team2129.lib.schedule.Command;

public class WaitForAngleCommand implements Command {

    private final Gyroscope gyro;

    public WaitForAngleCommand(Gyroscope gyro) {
        this.gyro = gyro;
    }

    @Override
    public boolean run() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
