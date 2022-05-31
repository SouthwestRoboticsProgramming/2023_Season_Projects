package com.swrobotics.robot.subsystem;

import com.kauailabs.navx.frc.AHRS;
import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.swerve.SwerveDrive;
import com.swrobotics.lib.swerve.SwerveDriveSpecialties;
import com.swrobotics.lib.swerve.SwerveModule;

public class Swerve {
    private final SwerveDrive swerve;
    
    public Swerve() {
        AHRS navx = new AHRS();
        Gyroscope gyro = new Gyroscope() {

            @Override
            public Angle getRawAngle() {
                return Angle.cwDeg(navx.getAngle());
            }
            
        };

        SwerveModule modules[] = {
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4)),
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4)),
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4)),
            new SwerveModule(new SwerveHelper(1,2,3, new Vec2d(5,4))
        };
        swerve = new SwerveDrive(modules, gyro, SwerveDriveSpecialties.L1);
    }

}