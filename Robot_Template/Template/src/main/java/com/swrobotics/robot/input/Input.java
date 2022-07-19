package com.swrobotics.robot.input;

import static com.swrobotics.robot.Constants.*;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.MathUtil;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.util.InputUtils;
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

        Vec2d raw = new Vec2d(x, y);

        // Extract angle
        Angle angle = raw.angle();
        if (raw.x == 0 && raw.y == 0) {
            angle = Angle.cwDeg(0);
        }

        // Create a vector with filtered magnitude, and direction.
        return new Vec2d(angle, MathUtil.clamp(filter.calculate(raw.magnitude()), -MAX_DRIVE_SPEED, MAX_DRIVE_SPEED));
    }

    public Angle getDriveRotation() {
        return Angle.cwDeg(InputUtils.applyDeadband(controller.getRightX(), DEADBAND) * MAX_DRIVE_ROTATION);
    }
}
