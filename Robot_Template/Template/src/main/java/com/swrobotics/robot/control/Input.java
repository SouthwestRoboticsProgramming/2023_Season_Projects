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

    private final XboxController controller;

    private final Toggle intakeToggle;

    public Input() {
        controller = new XboxController(0);

        intakeToggle = new Toggle();
        intakeToggle.set(false);
    }

    public double getExample() { // This is how input functions are usually formatted
        return controller.getLeftTriggerAxis();
    }

    public Vec2d getDriveTranslation() {
        // Apply deadband
        double x = MathUtil.applyDeadband(controller.getLeftX(), DEADBAND);
        double y = -MathUtil.applyDeadband(controller.getLeftY(), DEADBAND);

        return new Vec2d(x, y).mul(MAX_DRIVE_SPEED.get());
    }

    public Angle getDriveRotation() {
        return Angle.cwRad(MathUtil.applyDeadband(controller.getRightX(), DEADBAND) * MAX_DRIVE_ROTATION.get());
    }

    public boolean getFieldRelative() {
        return !(controller.getLeftTriggerAxis() > 1.0 - DEADBAND);
    }

    public boolean getSlowMode() {
        return controller.getRightBumper();
    }


    // Intake
    public boolean getIntakeOn() {
        intakeToggle.toggle(controller.getYButtonPressed());
        return intakeToggle.get();
    }

    // Thrower
    public boolean getAim() {
        return controller.getLeftBumper();
    }

    public boolean getShoot() {
        return controller.getAButtonPressed();
    }

    public boolean getAimOverride() {
        return controller.getStartButton();
    }

    public boolean getClimbNextStep() {
        return controller.getXButtonPressed();
    }

    public boolean getClimbPreviousStep() {
        return controller.getBButtonPressed();
    }
}
