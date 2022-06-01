package com.swrobotics.lib.swerve;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.Motor;

public interface SwerveModuleHelper {

    // TODO: Document
    
    public Motor getDriveMotor();

    public Motor getTurnMotor();

    public AbsoluteEncoder getEncoder();

    public Vec2d getPosition();

    
}
