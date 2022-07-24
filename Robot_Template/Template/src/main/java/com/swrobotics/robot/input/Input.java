package com.swrobotics.robot.input;

import static com.swrobotics.robot.Constants.*;

import com.swrobotics.lib.util.InputUtils;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.MathUtil;
import com.team2129.lib.math.Vec2d;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.XboxController;


public class Input {
    private static final double DEADBAND = 0.2;

    private final XboxController controller;

    private final SlewRateLimiter filter;
    
    public Input() {
        controller = new XboxController(0);

        filter = new SlewRateLimiter(INPUT_ACCELERATION);
    }

    public double getExample() { // This is how input functions are usually formatted
        return controller.getLeftTriggerAxis();
    }

    public Vec2d getDriveTranslation() {
        // Apply deadband
        double x = InputUtils.applyDeadband(controller.getLeftX(), DEADBAND);
        double y = -InputUtils.applyDeadband(controller.getLeftY(), DEADBAND);

        return new Vec2d(x, y).mul(MAX_DRIVE_SPEED);
    }

    public Angle getDriveRotation() {
        return Angle.cwDeg(InputUtils.applyDeadband(controller.getRightX(), DEADBAND) * MAX_DRIVE_ROTATION);
    }

    public boolean getFieldRelative() {
        return !(controller.getLeftTriggerAxis() > 1.0 - DEADBAND);
    }

    public boolean getSlowMode() {
        return controller.getRightBumper();
    }
}
