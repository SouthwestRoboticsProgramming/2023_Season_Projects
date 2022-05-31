package com.swrobotics.lib.swerve;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.NewMotor;

public interface SwerveModuleHelper {
    
    public NewMotor getDriveMotor();

    public NewMotor getTurnMotor();

    public AbsoluteEncoder getEncoder();

    public Vec2d getPosition();

    
}
