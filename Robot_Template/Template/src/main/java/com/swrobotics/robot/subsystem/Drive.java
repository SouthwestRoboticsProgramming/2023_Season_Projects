package com.swrobotics.robot.subsystem;

import static com.swrobotics.robot.Constants.*;

import com.team2129.lib.gyro.ADIS16448Gyroscope;
import com.swrobotics.robot.control.Input;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveDrive;
import com.team2129.lib.swerve.SwerveModule;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.gyro.Gyroscope;

/*
 * Wheel Layout:
 * 
 * w0 ------- w1
 *  |    ^    |
 *  |    |    |
 *  |    |    |
 * w2 ------- w3
 */

public class Drive implements Subsystem {
    
    private final Input input;
    private final Gyroscope gyro;
    private final SwerveDrive drive;

    public Drive(Input input) {
        this.input = input;
        
        gyro = new ADIS16448Gyroscope();
        
        double centerDistance = WHEEL_SPACING / 2;

        SwerveModule w0 = SwerveModuleMaker.buildModule(this, 5, 11, 10, Angle.cwDeg(24.873 + 180), new Vec2d(-centerDistance, -centerDistance));
        SwerveModule w1 = SwerveModuleMaker.buildModule(this, 3, 12, 8, Angle.cwDeg(79.541 + 180), new Vec2d(-centerDistance, centerDistance));
        SwerveModule w2 = SwerveModuleMaker.buildModule(this, 1, 13, 6, Angle.cwDeg(132.188), new Vec2d(centerDistance, -centerDistance));
        SwerveModule w3 = SwerveModuleMaker.buildModule(this, 4, 14, 9, Angle.cwDeg(183.604), new Vec2d(centerDistance, centerDistance));
        
        drive = new SwerveDrive(gyro, MAX_WHEEL_VELOCITY, w0, w1, w2, w3);
    }

    @Override
    public void periodic() {
//        drive.printEncoderOffsets();
    }

    @Override
    public void teleopPeriodic() {
        Vec2d translation = input.getDriveTranslation();
        Angle rotation = input.getDriveRotation();

        if (input.getSlowMode()) {
            translation.mul(0.5);
        }

        drive.setMotion(translation, rotation, input.getFieldRelative());
    }
}
