package com.swrobotics.robot.auto;

import com.swrobotics.robot.subsystem.drive.Drive;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.schedule.Command;
import com.team2129.lib.time.Duration;
import com.team2129.lib.time.Timestamp;

public final class AutoDriveForTime implements Command {
    private final Drive drive;
    private final DriveAutoInput input;
    private final Timestamp finishTime;

    public AutoDriveForTime(Drive drive, DriveAutoInput input, Duration dur) {
        this.drive = drive;
        this.input = input;
        this.finishTime = Timestamp.now().after(dur);
    }

    @Override
    public void end(boolean cancelled) {
        drive.clearAutoInput();
    }

    @Override
    public boolean run() {
        drive.setAutoInput(input);
        return Timestamp.now().isAtOrAfter(finishTime);
    }
}
