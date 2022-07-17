package com.swrobotics.robot.input;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.MathUtil;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.util.InputUtils;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.XboxController;

import static com.swrobotics.robot.Constants.*;

public class Input {
    private static final double DEADBAND = 0.1;

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
        double x = InputUtils.applyDeadband(controller.getLeftX(), DEADBAND);
        double y = InputUtils.applyDeadband(controller.getLeftY(), DEADBAND);

        Vec2d raw = new Vec2d(x, y);

        return new Vec2d(raw.angle(), MathUtil.map(filter.calculate(raw.magnitude()), -Math.sqrt(2), Math.sqrt(2), -MAX_DRIVE_SPEED, MAX_DRIVE_SPEED));
    }

    public Angle getDriveRotation() {
        return Angle.cwDeg(InputUtils.applyDeadband(controller.getRightX(), DEADBAND * MAX_DRIVE_ROTATION));
    }
}
