package com.swrobotics.robot.control;

import com.team2129.lib.utils.Toggle;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.MathUtil;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.net.NTDouble;

import edu.wpi.first.wpilibj.XboxController;


public class Input {
    private static final double DEADBAND = 0.2;

    public static final NTDouble MAX_DRIVE_ROTATION = new NTDouble("Drive/Max_Rotation_Speed", 0.5 * Math.PI); // Radians / Second
    public static final NTDouble MAX_DRIVE_SPEED = new NTDouble("Drive/Max_Drive_Speed", 4.11); // Meters / Second

    private final XboxController driver;
    private final XboxController manipulator;

    private final Toggle intakeToggle;

    public Input() {
        driver = new XboxController(0);
        manipulator = new XboxController(1);

        intakeToggle = new Toggle();
        intakeToggle.set(false);
    }

    /* Drive */
    public Vec2d getDriveTranslation() { // Driver left stick
        // Apply deadband
        double x = MathUtil.applyDeadband(driver.getLeftX(), DEADBAND);
        double y = -MathUtil.applyDeadband(driver.getLeftY(), DEADBAND);

        return new Vec2d(x, y).mul(MAX_DRIVE_SPEED.get());
    }

    public Angle getDriveRotation() { // Driver right stick
        return Angle.cwRad(MathUtil.applyDeadband(driver.getRightX(), DEADBAND) * MAX_DRIVE_ROTATION.get());
    }

    public boolean getFieldRelative() { // Driver right trigger (Fully pulled)
        return !(driver.getRightTriggerAxis() > 1.0 - DEADBAND);
    }

    public boolean getSlowMode() { // Driver left trigger (Fully pulled)
        return !(driver.getLeftTriggerAxis() > 1.0 - DEADBAND);
    }


    /* Intake */
    public boolean getIntakeOn() { // Manipulator toggle Y button
        intakeToggle.toggle(manipulator.getYButtonPressed());
        return intakeToggle.get();
    }

    /* Thrower */
    public boolean getAim() { // Either right bumper
        return driver.getRightBumper() || manipulator.getRightBumper();
    }

    public boolean getShoot() { // Manipulator (Per ball) A button
        return manipulator.getAButtonPressed();
    }

    public boolean getAimLow() { // Not currently a feature
        return false;
    }

    public boolean getAimOverride() { // Either tart button
        return driver.getStartButton() || manipulator.getStartButton();
    }

    /* Climber */
    public boolean getClimbNextStep() { // Manipulator X button
        return manipulator.getXButtonPressed();
    }

    public boolean getClimbPreviousStep() { // Manipulator B button
        return manipulator.getBButtonPressed();
    }
}
