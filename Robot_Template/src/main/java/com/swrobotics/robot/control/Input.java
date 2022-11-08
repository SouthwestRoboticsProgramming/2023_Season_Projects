package com.swrobotics.robot.control;

import com.swrobotics.lib.input.XboxController;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.MathUtil;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.lib.utils.Toggle;

import com.swrobotics.robot.subsystem.thrower.commands.ShootCommand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class Input implements Subsystem {
    private static final double DEADBAND = 0.2;

    public static final NTDouble MAX_DRIVE_ROTATION = new NTDouble("Drive/Max_Rotation_Speed", 0.5 * Math.PI); // Radians / Second
    public static final NTDouble MAX_DRIVE_SPEED = new NTDouble("Drive/Max_Drive_Speed", 4.11); // Meters / Second

    private final XboxController driver;
    private final XboxController manipulator;

    private final Toggle intakeToggle;

    public Input() {
        driver = new XboxController(0);
        manipulator = new XboxController(1);
        Scheduler.get().addSubsystem(this, driver);
        Scheduler.get().addSubsystem(this, manipulator);

        intakeToggle = new Toggle();
        intakeToggle.set(false);
    }

    /* Drive */
    public Vec2d getDriveTranslation() { // Driver left stick
        // Apply deadband
        double x = MathUtil.applyDeadband(driver.leftStickX.get(), DEADBAND);
        double y = -MathUtil.applyDeadband(driver.leftStickY.get(), DEADBAND);

        return new Vec2d(x, y).mul(MAX_DRIVE_SPEED.get());
    }

    public Angle getDriveRotation() { // Driver right stick
        return Angle.cwRad(MathUtil.applyDeadband(driver.rightStickX.get(), DEADBAND) * MAX_DRIVE_ROTATION.get());
    }

    public boolean getFieldRelative() { // Driver right trigger (Fully pulled)
        return !(driver.rightTrigger.get() > 1.0 - DEADBAND);
    }

    public boolean getSlowMode() { // Driver left trigger (Fully pulled)
        return (driver.leftTrigger.get() > 1.0 - DEADBAND);
    }


    /* Intake */
    public boolean getIntakeOn() { // Manipulator toggle Y button
        intakeToggle.toggle(manipulator.y.isRising());
        return intakeToggle.get();
    }

    /* Thrower */

    public boolean getAim() { // Either right bumper
        boolean out = driver.rightBumper.isPressed() || manipulator.rightBumper.isPressed();
        double rumble = out ? 0.75 : 0;
        driver.setRumble(rumble);
        manipulator.setRumble(rumble);
        return out;
    }



    public boolean getShoot() { // Manipulator (Per ball) A button
        return manipulator.a.isRising();
    }

    public boolean getAimLow() { // Not currently a feature
        return false;
    }

    public boolean getAimOverride() { // Either tart button
        return driver.start.isPressed() || manipulator.start.isPressed();
    }

    /* Climber */
    public boolean getClimbNextStep() { // Manipulator X button
        return manipulator.x.isRising();
    }

    public boolean getClimbPreviousStep() { // Manipulator B button
        return manipulator.b.isRising();
    }
}
