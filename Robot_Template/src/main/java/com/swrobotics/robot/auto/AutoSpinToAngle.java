package com.swrobotics.robot.auto;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.CCWAngle;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.robot.subsystem.drive.Drive;

import edu.wpi.first.math.trajectory.Trajectory;

public class AutoSpinToAngle implements Command {
    private final Drive drive;
    private final CCWAngle angle;

    public AutoSpinToAngle(Drive drive, Angle angle, boolean robotRelative) {
        this.drive = drive;
        if (robotRelative) {
            angle = angle.ccw().add(drive.getRotation().ccw()); // Make it relative to the local angle
        }

        this.angle = angle.ccw();
    }

    @Override
    public boolean run() {
        drive.setTrajectory(new Trajectory.State(), angle);
        return false;
    }
    
}
