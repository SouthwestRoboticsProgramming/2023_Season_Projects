package com.swrobotics.robot.subsystem.drive;

import com.swrobotics.lib.gyro.NavX;
import com.swrobotics.lib.math.*;
import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.messenger.MessageReader;
import com.swrobotics.lib.messenger.MessengerClient;
import com.swrobotics.lib.net.NTBoolean;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.net.NTEnum;
import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.lib.swerve.SwerveDrive;
import com.swrobotics.lib.swerve.SwerveModule;
import com.swrobotics.lib.wpilib.AbstractRobot;
import com.swrobotics.lib.wpilib.RobotState;
import com.swrobotics.robot.auto.DriveAutoInput;
import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.Localization;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

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

    private static final NTDouble TURN_STOP_TOL = new NTDouble("Swerve/Turn stop tolerance", 80);
    private static final NTDouble TURN_FULL_TOL = new NTDouble("Swerve/Turn full tolerance", 7);

    private static final String MSG_GET_MODULE_DEFS = "Swerve:GetModuleDefs";
    private static final String MSG_MODULE_DEFS = "Swerve:ModuleDefs";
    private static final String MSG_MODULE_STATES = "Swerve:ModuleStates";

    private final Input input;
    private final MessengerClient msg;

    private final SwerveDrive drive;
    private final SwerveModule[] modules;
    private final NavX gyro;
    private Localization loc;

    public Drive(Input input, NavX gyro, MessengerClient msg) {
        this.input = input;
        this.gyro = gyro;
        this.msg = msg;

        SwerveModule w0 = SwerveModuleMaker.buildModule(this, SLOT_0_MODULE.get(), TURN_ID_0, SLOT_0_POS, 0);
        SwerveModule w1 = SwerveModuleMaker.buildModule(this, SLOT_1_MODULE.get(), TURN_ID_1, SLOT_1_POS, 90);
        SwerveModule w2 = SwerveModuleMaker.buildModule(this, SLOT_2_MODULE.get(), TURN_ID_2, SLOT_2_POS, 270);
        SwerveModule w3 = SwerveModuleMaker.buildModule(this, SLOT_3_MODULE.get(), TURN_ID_3, SLOT_3_POS, 180);
        modules = new SwerveModule[] {w0, w1, w2, w3};

        drive = new SwerveDrive(gyro, MAX_WHEEL_VELOCITY, TURN_STOP_TOL, TURN_FULL_TOL, modules);

        Scheduler.get().addSubsystem(this, drive);

        msg.addHandler(MSG_GET_MODULE_DEFS, this::onGetModuleDefs);
    }

    public void setLocalization(Localization loc) {
        this.loc = loc;
    }

    private void onGetModuleDefs(String type, MessageReader reader) {
        MessageBuilder builder = msg.prepare(MSG_MODULE_DEFS);
        builder.addInt(modules.length);
        for (SwerveModule module : modules) {
            Vec2d pos = module.getPosition();
            builder.addDouble(pos.x);
            builder.addDouble(pos.y);
        }
        builder.send();
    }

    public Angle getRotation() {
        return CoordinateConversions.fromWPIAngle(drive.getOdometryPose().getRotation());
    }

    public Vec2d getPosition() {
        return CoordinateConversions.fromWPICoords(drive.getOdometryPose().getTranslation());
    }

    public void setPose(Vec2d position, Angle rotation) {
        drive.setOdometryPose(
            new Pose2d(
                CoordinateConversions.toWPICoords(position),
                CoordinateConversions.toWPIAngle(rotation)
            )
        );
    }

    public void set(Vec2d translation, Angle rotationsPerSecond, boolean fieldRelative) {
        drive.setMotion(translation, rotationsPerSecond, fieldRelative);
    }

    @Override
    public void periodic() {
        if (PRINT_ENCODER_OFFSETS.get()) {
            drive.printEncoderOffsets();
        }

        MessageBuilder builder = msg.prepare(MSG_MODULE_STATES);
        for (SwerveModule module : modules) {
            SwerveModuleState current = module.getState();
            SwerveModuleState target = module.getTargetState();

            // Possible if no target state has been specified yet
            if (target == null)
                continue;

            builder.addDouble(target.angle.getRadians());
            builder.addDouble(current.angle.getRadians());
            builder.addDouble(target.speedMetersPerSecond);
            builder.addDouble(current.speedMetersPerSecond);
        }
        builder.send();

    }
    
    @Override
    public void teleopPeriodic() {
        Vec2d translation = input.getDriveTranslation();
        Angle rotation = input.getDriveRotation();
        boolean fieldRelative = input.getFieldRelative();

        if (input.getSlowMode()) {
            translation.mul(0.5);
        }

        if (input.getAim()) {
            // TODO
        }

        set(translation, rotation, fieldRelative);
    }
}
