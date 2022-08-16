package com.swrobotics.robot.subsystem;

import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;

public class Localization {
    public static final Vec2d HUB_POS = new Vec2d(2, 5);
    
    public Vec2d getPosition() {
        return new Vec2d();
    }

    public double getMetersToHub() {
        return 5;
        //return getPosition().set(HUB_POS).magnitude();
    }

    public double getFeetToHub() {
        return 10;
    }

    public Angle getAngleToTarget() {
        return getPosition().angleTo(HUB_POS);
    }

    public boolean isLookingAtTarget() {
        return true;
    }
}
