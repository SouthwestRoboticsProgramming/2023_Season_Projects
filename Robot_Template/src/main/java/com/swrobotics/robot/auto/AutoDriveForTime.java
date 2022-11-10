package com.swrobotics.robot.auto;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.lib.time.Duration;
import com.swrobotics.lib.time.Timestamp;
import com.swrobotics.robot.subsystem.drive.Drive;

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
        drive.set(new Vec2d(), Angle.ZERO, false);
    }

    @Override
    public boolean run() {
        drive.set(input.getTranslation(), input.getRotation(), input.getMode() == DriveAutoInput.Mode.FIELD_RELATIVE);
        return Timestamp.now().isAtOrAfter(finishTime);
    }
}
