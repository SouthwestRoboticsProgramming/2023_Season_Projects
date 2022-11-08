package com.swrobotics.robot.subsystem;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.net.NTBoolean;
import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.lib.wpilib.RobotState;
import com.swrobotics.robot.Robot;
import com.swrobotics.robot.subsystem.drive.Drive;

public class Localization implements Subsystem {

    private static final NTBoolean USE_LIMELIGHT = new NTBoolean("Limelight/Use for localization", true);

    private final Limelight limelight;
    private final Drive drive;

    /**
     * In meters
     */
    private Vec2d position;
    private Angle angle;

    public Localization(Drive drive) {
        limelight = new Limelight();
        this.drive = drive;
        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, limelight);
        position = new Vec2d();
        angle = Angle.zero();
    }

    public Vec2d getPosition() {
        return position;
    }

    public double getFeetToHub() {
        return position.magnitude() * 3.281; // Convert meters to feet
    }

    public double getMetersToHub() {
        return position.magnitude();
    }

    public Angle getAngleToHub() {
        double deg = position.angle().getCWDeg() - 90;
        if (deg < -180)
            deg += 360;
        if (deg > 180)
            deg -= 360;
        return Angle.cwDeg(deg);
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

    public boolean isLimelightAccurate() {
        return limelight.isAccurate();
    }

    @Override
    public void periodic() {

        // Update angle with new gyro position
        angle = drive.getRotation();

        // Update position using limelight
        // Only update if it is accurate, the robot is in teleop, and we actually want to use it.
        if (limelight.isAccurate() && USE_LIMELIGHT.get() && Robot.get().getCurrentState() != RobotState.DISABLED) {
            position = calculatePositionOnLimelight();
            drive.setPosition(position);
        } else {
            position = drive.getPosition();
        }

        // System.out.println("Pos: " + position);
    }

    
}
