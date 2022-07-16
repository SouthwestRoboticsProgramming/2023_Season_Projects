package com.swrobotics.robot.subsystem;

import com.swrobotics.lib.gyro.ADIS16448Gyroscope;
import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.routine.Routine;
import com.swrobotics.lib.swerve.SwerveDrive;
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
        drive = new SwerveDrive(gyro, MAX_WHEEL_VELOCITY,
            SwerveModuleMaker.buildModule(5, 11, 10, Angle.cwDeg(88.945 + 180), new Vec2d(-centerDistance, centerDistance)),
            SwerveModuleMaker.buildModule(5, 11, 10, Angle.cwDeg(349.453 - 180), new Vec2d(centerDistance, centerDistance)),
            SwerveModuleMaker.buildModule(5, 11, 10, Angle.cwDeg(42.100 - 90), new Vec2d(-centerDistance, -centerDistance)),
            SwerveModuleMaker.buildModule(5, 11, 10, Angle.cwDeg(94.746 - 90), new Vec2d(centerDistance, -centerDistance))
        );
    }

    @Override
    public void periodic() {
        Vec2d translation = input.getDriveTranslation();
        Angle rotation = input.getDriveRotation();

        drive.setMotion(translation, rotation);
    }
}
