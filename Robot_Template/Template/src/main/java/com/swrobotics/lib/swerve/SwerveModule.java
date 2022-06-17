package com.swrobotics.lib.swerve;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.encoder.CANCoderImplementation;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.encoder.TalonInternalEncoder;
import com.swrobotics.lib.motor.implementations.TalonMotor;

/**
 * A single swerve module that controls both speed and azimuth (steer).
 */
public class SwerveModule {

    private final TalonMotor drive;
    private final TalonMotor steer;
    
    private final Encoder driveEncoder;
    private final AbsoluteEncoder steerEncoder;

    public SwerveModule(BaseTalon drive_toWrap, BaseTalon steer_toWrap, Encoder driveEncoder, AbsoluteEncoder steerEncoder) {

       // Setup encoders
        this.driveEncoder = driveEncoder;
        this.steerEncoder = steerEncoder;


        drive = new TalonMotor(drive_toWrap).assignEncoder(driveEncoder);
        steer = new TalonMotor(steer_toWrap).assignEncoder(steerEncoder);
    }
}
