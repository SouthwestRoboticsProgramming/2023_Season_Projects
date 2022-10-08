package com.swrobotics.robot.subsystem;

import com.swrobotics.robot.Robot;
import com.team2129.lib.gyro.NavX;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.wpilib.RobotState;

public class Localization implements Subsystem {

    private static final NTBoolean USE_LIMELIGHT = new NTBoolean("Limelight/Use for localization", true);

    private final Limelight limelight;
    private final NavX navx;

    /**
     * In meters
     */
    private Vec2d position;
    private Angle angle;

    public Localization() {
        limelight = new Limelight();
        navx = new NavX();

        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, limelight);
        position = new Vec2d();
        angle = Angle.zero();
    }

    private Vec2d calculatePositionOnLimelight() {
        double limelightAngleDeg = limelight.getXAngle().getCWDeg();
        double limelightDistance = limelight.getDistance();
        double robotAngleDeg = angle.getCWDeg();

        Angle angleDiff = Angle.cwDeg(robotAngleDeg - limelightAngleDeg);
        angleDiff = Angle.cwDeg(angleDiff.getCWDeg() - 90); // Account for position shenanigans
        
        limelightDistance += 1.35582 / 2; // Account for hub radius
        double fieldX = -limelightDistance * Math.cos(angleDiff.getCWDeg());
        double fieldY = limelightDistance * Math.sin(angleDiff.getCWDeg());

        return new Vec2d(fieldX, fieldY);
    }

    @Override
    public void periodic() {

        // Update angle with new gyro position
        angle = navx.getAngle();

        // Update position using limelight
        // Only update if it is accurate, the robot is in teleop, and we actually want to use it.
        if (limelight.isAccurate() && USE_LIMELIGHT.get() && Robot.get().getCurrentState() == RobotState.TELEOP) {
            position = calculatePositionOnLimelight();
        }

        System.out.println("Pos: " + position);
    }

    
}
