package com.swrobotics.robot.auto;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.robot.subsystem.Localization;
import com.swrobotics.robot.subsystem.drive.Drive;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.Collections;
import java.util.List;

/**
 * Command to follow a path calculated by the pathfinder.
 */
public final class AutoDriveToPoint implements Command {
    private static final NTDouble VELOCITY = new NTDouble("Auto/Path/Velocity", 1);
    private static final NTDouble TOLERANCE = new NTDouble("Auto/Path/Tolerance", 0.075);

    private static AutoDriveToPoint current = null;

    private final Pathfinder finder;
    private final Localization loc;
    private final Drive drive;
    private final Vec2d targetPos;
    private final Angle targetAngle;

    public AutoDriveToPoint(Pathfinder finder, Localization loc, Drive drive, Vec2d targetPos, Angle targetAngle) {
        this.finder = finder;
        this.loc = loc;
        this.drive = drive;
        this.targetPos = targetPos;
        this.targetAngle = targetAngle;
    }

    @Override
    public void init() {
        // Make sure this is the only instance running
        if (current != null) {
            DriverStation.reportWarning("Only one path can be followed at a time, cancelling previous path command", false);
            Scheduler.get().removeCommand(current);
        }

        current = this;
        finder.setTarget(targetPos);
    }

    @Override
    public void end(boolean cancelled) {
        current = null;
    }

    @Override
    public boolean run() {
        List<Vec2d> path;
        if (finder.hasPathForCurrentTarget()) {
            path = finder.getPath();
        } else {
            // If we don't have a calculated path, try a straight line
            path = Collections.singletonList(targetPos);
        }

        // If the path is empty, we don't need to continue
        if (path.isEmpty())
            return true;

        // Find the first point that is out of tolerance
        Vec2d currentPoint = loc.getPosition();
        double tol = TOLERANCE.get();
        tol *= tol;
        for (Vec2d point : path) {
            // Skip points that are already within tolerance
            Vec2d error = point.sub(currentPoint, new Vec2d());
            if (error.magnitudeSq() <= tol)
                continue;

            // Drive to the point
            Vec2d translation = error.normalize().mul(VELOCITY.get());
            drive.set(translation, Angle.ZERO, true); // FIXME: Add some way to target the angle to Drive
            return false;
        }

        // If all points in the path are within tolerance, we have reached the end
        return true;
    }
}
