package com.swrobotics.robot.subsystem;

import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.drive.Drive;
import com.team2129.lib.gyro.NavX;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveDrive;

import edu.wpi.first.math.util.Units;

public class Localization implements Subsystem {
    public static final Vec2d HUB_POS = new Vec2d(2, 5);

    public static final NTDouble CAMERA_FOV = new NTDouble("Localization/Camera_FOV_Deg", 200);

    private final SwerveDrive swerve;
    private final NavX gyro;
    private final Limelight limelight;
    private final Input input;

    private Vec2d currentPosition;

    public Localization(Drive drive, Limelight limelight, Input input) {
        swerve = drive.getDriveController();
        gyro = drive.getNavX();
        this.limelight = limelight;
        this.input = input;
        currentPosition = new Vec2d();
    }
    
    public Vec2d getPosition() {
        return currentPosition;
    }

    public Angle getRotation() {
        return gyro.getAngle();
    }

    
    public Angle getAngleToTarget() {
        return getPosition().angleTo(HUB_POS);
    }

    public Angle getLocalAngleToTarget() {
        return gyro.getAngle()
        .sub(getAngleToTarget())
        .normalizeDeg(-90, 90); // Almost certainly not right
    }


    public double getMetersToHub() {
        return getPosition().set(HUB_POS).magnitude();
    }

    public double getFeetToHub() {
        return Units.metersToFeet(getMetersToHub());
    }

    public boolean cameraCanSeeTarget() {
        return Math.abs(getAngleToTarget().getCWDeg()) < CAMERA_FOV.get();
    }

    @Override
    public void periodic() {
        
        // If the camera can't see the target
        if (!(cameraCanSeeTarget() || input.getAimOverride() && limelight.isAccurate())) {
            currentPosition = swerve.getOdometryPose(); // Use just odometry
            return;
        }

        // Get camera readings
        double distance = limelight.getDistance();
        Angle cameraAngle = limelight.getXAngle();
        Angle gyroAngle = gyro.getAngle();

        Angle angleDiff = gyroAngle.sub(cameraAngle);
        // Other one had -= 90. Why?

        currentPosition = new Vec2d(
            distance * angleDiff.getCos(),
            distance * angleDiff.getSin()
        );

        // TODO: Ryan, link the desmos, or I can find it

        swerve.setOdometryPosition(currentPosition);
        


        Subsystem.super.periodic();
    }
}
