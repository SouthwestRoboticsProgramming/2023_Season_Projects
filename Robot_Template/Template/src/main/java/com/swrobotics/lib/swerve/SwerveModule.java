package com.swrobotics.lib.swerve;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
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

    /**
     * Create a swerve module with already-configured motors and encoders
     * @param drive_toWrap
     * @param steer_toWrap
     * @param driveEncoder
     * @param steerEncoder
     */
    public SwerveModule(BaseTalon drive_toWrap, BaseTalon steer_toWrap, Encoder driveEncoder, AbsoluteEncoder steerEncoder) {

       // Setup encoders
        this.driveEncoder = driveEncoder;
        this.steerEncoder = steerEncoder;

        // Create motors and assign encoders
        drive = new TalonMotor(drive_toWrap);
        drive.assignEncoder(driveEncoder);

        steer = new TalonMotor(steer_toWrap);
        steer.assignEncoder(steerEncoder);


    }
}
