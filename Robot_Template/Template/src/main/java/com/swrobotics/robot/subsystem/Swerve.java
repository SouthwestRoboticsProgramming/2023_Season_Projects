package com.swrobotics.robot.subsystem;

import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.swerve.SwerveDrive;
import com.swrobotics.lib.swerve.SwerveModule;

public class Swerve {
    private final SwerveDrive swerve;
    
    public Swerve() {
        SwerveModule modules[] = {
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4))),
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4))),
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4))),
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4)))
        };

        swerve = new SwerveDrive(modules);
    }

}