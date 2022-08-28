package com.swrobotics.robot.subsystem;

import com.team2129.lib.gyro.NavX;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.net.NTDouble;

import edu.wpi.first.math.util.Units;

public class Localization {
    public static final Vec2d HUB_POS = new Vec2d(2, 5);

    public static final NTDouble CAMERA_FOV = new NTDouble("Localization/Camera_FOV_Deg", 200);

    private final NavX gyro;

    public Localization() {
        gyro = new NavX();
    }
    
    public Vec2d getPosition() {
        return new Vec2d();
    }

    public Angle getRotation() {
        return gyro.getAngle();
    }

    
    public Angle getAngleToTarget() {
        return getPosition().angleTo(HUB_POS);
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
}
