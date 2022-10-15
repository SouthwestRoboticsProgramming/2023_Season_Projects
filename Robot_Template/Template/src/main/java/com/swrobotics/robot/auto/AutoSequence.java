package com.swrobotics.robot.auto;

import com.swrobotics.robot.subsystem.drive.Drive;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.schedule.CommandSequence;
import com.team2129.lib.time.Duration;
import com.team2129.lib.time.TimeUnit;

public final class AutoSequence extends CommandSequence {
    public AutoSequence(Drive drive) {
        append(new AutoDriveForTime(
            drive,
            new DriveAutoInput(
                new Vec2d(0, -2),
                Angle.zero(),
                false // Robot relative
            ), new Duration(2.5, TimeUnit.SECONDS)
        ));
    }
}
