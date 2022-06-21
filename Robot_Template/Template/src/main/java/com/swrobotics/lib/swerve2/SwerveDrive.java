package com.swrobotics.lib.swerve2;

import com.swrobotics.lib.gyro.Gyroscope;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public final class SwerveDrive {
    private final Gyroscope gyro;
    private final SwerveDriveKinematics kinematics;
    private final SwerveModule[] modules;
    private final double maxWheelSpeed;

    public SwerveDrive(Gyroscope gyro, double maxWheelSpeed, SwerveModule... modules) {
        this.gyro = gyro;
        this.maxWheelSpeed = maxWheelSpeed;
        Translation2d[] positions = new Translation2d[modules.length];
        for (int i = 0; i < modules.length; i++) {
            positions[i] = modules[i].getCenterLocalPosition().toTranslation2d();
        }
        kinematics = new SwerveDriveKinematics(positions);
        this.modules = modules;
    }

    // Translation in meters per second, turn in rotations per second
    public void drive(Vec2d translation, Angle turn) {
        ChassisSpeeds speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
                translation.x,
                translation.y,
                turn.getCCWRad(),
                gyro.getAngle().toRotation2dCCW()
        );

        SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxWheelSpeed);

        for (int i = 0; i < states.length; i++) {
            modules[i].update(states[i]);
        }

        String str = "";
        for (SwerveModule module : modules) {
            str += String.format("%3.3f ", module.getEncoderAngle().getCCWDeg());
        }
        System.out.println(str);
    }
}
