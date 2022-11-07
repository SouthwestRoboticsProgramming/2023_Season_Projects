package com.swrobotics.robot.subsystem.climber.sequence;

import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.schedule.Command;

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
