package com.swrobotics.robot.subsystem.drive;

import com.swrobotics.robot.control.Input;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveDrive;
import com.team2129.lib.swerve.SwerveModule;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.net.NTEnum;
import com.team2129.lib.gyro.NavX;

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
    private static final double WHEEL_SPACING = 0.4699; // Meters
    private static final double CENTER_DISTANCE = WHEEL_SPACING / 2;

    public static final double MAX_WHEEL_VELOCITY = 4.11;

    private static final Vec2d SLOT_0_POS = new Vec2d(-CENTER_DISTANCE,  CENTER_DISTANCE);
    private static final Vec2d SLOT_1_POS = new Vec2d( CENTER_DISTANCE,  CENTER_DISTANCE);
    private static final Vec2d SLOT_2_POS = new Vec2d(-CENTER_DISTANCE, -CENTER_DISTANCE);
    private static final Vec2d SLOT_3_POS = new Vec2d( CENTER_DISTANCE, -CENTER_DISTANCE);
    
    // Note: These do not automatically update, the robot code needs to be restarted
    //       for changes to take effect
    private static final NTEnum<SwerveModuleDef> SLOT_0_MODULE = new NTEnum<>("Swerve/Slots/Slot 0", SwerveModuleDef.class, SwerveModuleDef.MODULE_1);
    private static final NTEnum<SwerveModuleDef> SLOT_1_MODULE = new NTEnum<>("Swerve/Slots/Slot 1", SwerveModuleDef.class, SwerveModuleDef.MODULE_2);
    private static final NTEnum<SwerveModuleDef> SLOT_2_MODULE = new NTEnum<>("Swerve/Slots/Slot 2", SwerveModuleDef.class, SwerveModuleDef.MODULE_3);
    private static final NTEnum<SwerveModuleDef> SLOT_3_MODULE = new NTEnum<>("Swerve/Slots/Slot 3", SwerveModuleDef.class, SwerveModuleDef.MODULE_4);

    private static final int TURN_ID_0 = 1;
    private static final int TURN_ID_1 = 2;
    private static final int TURN_ID_2 = 3;
    private static final int TURN_ID_3 = 4;

    private static final NTBoolean PRINT_ENCODER_OFFSETS = new NTBoolean("Swerve/Print Encoder Offsets", false);

    private final Input input;
    private final SwerveDrive drive;
    private final NavX gyro;

    public Drive(Input input) {
        this.input = input;
        gyro = new NavX();
        
        SwerveModule w0 = SwerveModuleMaker.buildModule(this, SLOT_0_MODULE.get(), TURN_ID_0, SLOT_0_POS);
        SwerveModule w1 = SwerveModuleMaker.buildModule(this, SLOT_1_MODULE.get(), TURN_ID_1, SLOT_1_POS);
        SwerveModule w2 = SwerveModuleMaker.buildModule(this, SLOT_2_MODULE.get(), TURN_ID_2, SLOT_2_POS);
        SwerveModule w3 = SwerveModuleMaker.buildModule(this, SLOT_3_MODULE.get(), TURN_ID_3, SLOT_3_POS);
        
        drive = new SwerveDrive(gyro, MAX_WHEEL_VELOCITY, w0, w1, w2, w3);
    }

    public SwerveDrive getDriveController() {
        return drive;
    }

    public NavX getNavX() {
        return gyro;
    }

    @Override
    public void periodic() {
        if (/*PRINT_ENCODER_OFFSETS.get()*/ false) {
            drive.printEncoderOffsets();
        }
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
