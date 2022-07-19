package com.swrobotics.robot.subsystem;

import com.swrobotics.lib.gyro.ADIS16448Gyroscope;
import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.routine.Routine;
import com.swrobotics.lib.swerve.SwerveDrive;
import com.swrobotics.lib.swerve.SwerveModule;
import com.swrobotics.robot.input.Input;

import static com.swrobotics.robot.Constants.*;

public class Drive extends Routine {
    
    private final Input input;
    private final Gyroscope gyro;
    private final SwerveDrive drive;

    public Drive(Input input) {
        this.input = input;
        
        gyro = new ADIS16448Gyroscope();
        
        double centerDistance = WHEEL_SPACING / 2;

        SwerveModule w1 = SwerveModuleMaker.buildModule(5, 11, 10, Angle.cwDeg(178.857 + 180), new Vec2d(-centerDistance, -centerDistance));
        SwerveModule w2 = SwerveModuleMaker.buildModule(3, 12, 8, Angle.cwDeg(78.923 + 180), new Vec2d(-centerDistance, centerDistance));
        SwerveModule w3 = SwerveModuleMaker.buildModule(4, 14, 9, Angle.cwDeg(3.164), new Vec2d(centerDistance, centerDistance));
        SwerveModule w4 = SwerveModuleMaker.buildModule(1, 13, 6, Angle.cwDeg(132.363), new Vec2d(centerDistance, -centerDistance));
        
        drive = new SwerveDrive(gyro, MAX_WHEEL_VELOCITY,
        w1,
        w2,
        w3,
        w4
        );
    }

    @Override
    public void periodic() {
        Vec2d translation = input.getDriveTranslation();
        Angle rotation = input.getDriveRotation();

        drive.setMotion(translation, rotation);
    }
}
