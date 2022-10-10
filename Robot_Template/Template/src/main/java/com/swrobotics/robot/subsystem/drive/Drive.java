package com.swrobotics.robot.subsystem.drive;

import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.Localization;
import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.swerve.SwerveDrive;
import com.team2129.lib.swerve.SwerveModule;
import com.team2129.lib.utils.CoordinateConversions;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.net.NTEnum;
import com.team2129.lib.gyro.NavX;

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

    private static final NTDouble BODY_SPIN_RAD = new NTDouble("Swerve/Body Spin/Rotation Speed Rad", 0.5 * Math.PI);

    private static final int TURN_ID_0 = 1;
    private static final int TURN_ID_1 = 2;
    private static final int TURN_ID_2 = 3;
    private static final int TURN_ID_3 = 4;

    private static final NTBoolean PRINT_ENCODER_OFFSETS = new NTBoolean("Swerve/Print Encoder Offsets", false);

    private static final String MSG_GET_MODULE_DEFS = "Swerve:GetModuleDefs";
    private static final String MSG_MODULE_DEFS = "Swerve:ModuleDefs";
    private static final String MSG_MODULE_STATES = "Swerve:ModuleStates";

    private final Input input;
    private final MessengerClient msg;

    private final SwerveDrive drive;
    private final SwerveModule[] modules;
    private final NavX gyro;
    private final Localization loc;

    public Drive(Input input, NavX gyro, Localization loc, MessengerClient msg) {
        this.input = input;
        this.gyro = gyro;
        this.msg = msg;
        this.loc = loc;
        
        SwerveModule w0 = SwerveModuleMaker.buildModule(this, SLOT_0_MODULE.get(), TURN_ID_0, SLOT_0_POS, 0);
        SwerveModule w1 = SwerveModuleMaker.buildModule(this, SLOT_1_MODULE.get(), TURN_ID_1, SLOT_1_POS, 90);
        SwerveModule w2 = SwerveModuleMaker.buildModule(this, SLOT_2_MODULE.get(), TURN_ID_2, SLOT_2_POS, 270);
        SwerveModule w3 = SwerveModuleMaker.buildModule(this, SLOT_3_MODULE.get(), TURN_ID_3, SLOT_3_POS, 180);
        modules = new SwerveModule[] {w0, w1, w2, w3};

        drive = new SwerveDrive(gyro, MAX_WHEEL_VELOCITY, modules);

        Scheduler.get().addSubsystem(this, drive);

        msg.addHandler(MSG_GET_MODULE_DEFS, this::onGetModuleDefs);
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

    public SwerveDrive getDriveController() {
        return drive;
    }

    public NavX getNavX() {
        return gyro;
    }

    public Angle getRotation() {
        return Angle.ccwDeg(drive.getOdometryPose().getRotation().getDegrees());
    }

    public Vec2d getPosition() {
        return CoordinateConversions.fromWPICoords(drive.getOdometryPose().getTranslation());
    }

    public void setPosition(Vec2d position) {
        drive.setOdometryPose(
            new Pose2d(
            CoordinateConversions.toWPICoords(position),
            getRotation().toRotation2dCCW()
            )
        );
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
        // Update localization
        loc.updateRoation(getRotation());
        loc.updatePosition(getPosition());

        // Update drive position if the limelight kicked in
        setPosition(loc.getPosition());

        Vec2d translation = input.getDriveTranslation();
        Angle rotation = input.getDriveRotation();

        if (input.getSlowMode()) {
            translation.mul(0.5);
        }

        if (input.getAim()) {
        double relativeAngle = loc.getAngleToHub().getCWDeg() - rotation.getCWDeg();
        
            if (relativeAngle > 0) {
                rotation = Angle.cwRad(BODY_SPIN_RAD.get());
            } else {
                rotation = Angle.cwRad(BODY_SPIN_RAD.get());
            }
        }

        drive.setMotion(translation, rotation, input.getFieldRelative());
    }
}
