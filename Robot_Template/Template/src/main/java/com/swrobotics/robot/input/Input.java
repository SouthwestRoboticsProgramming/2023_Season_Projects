package com.swrobotics.robot.input;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.util.InputUtils;
import edu.wpi.first.wpilibj.XboxController;

public class Input {
    private static final double DEADBAND = 0.1;

    private final XboxController controller;
    
    public Input() {
        controller = new XboxController(0);
    }

    public double getExample() { // This is how input functions are usually formatted
        return controller.getLeftTriggerAxis();
    }

    public Vec2d getDriveTranslation() {
        double x = InputUtils.applyDeadband(controller.getLeftX(), DEADBAND);
        double y = InputUtils.applyDeadband(controller.getLeftY(), DEADBAND);

        return new Vec2d(x, y).mul(0.25);
    }

    public Angle getDriveRotation() {
        // TODO: Make it work
        return Angle.cwDeg(0);//Angle.cwRad(0.01 * InputUtils.applyDeadband(controller.getRightX(), DEADBAND));
    }
}
