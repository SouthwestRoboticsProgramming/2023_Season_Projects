package com.swrobotics.bert.control;

import static com.swrobotics.bert.constants.InputConstants.*;

import com.swrobotics.bert.subsystems.Subsystem;
import com.swrobotics.bert.util.Utils;

// Note: The Y axes on the sticks are backwards from what you would expect: up is negative
public final class Input implements Subsystem {
    private final Controller drive;
    private final Controller manipulator;

    public Input() {
        drive = new XboxController(DRIVE_CONTROLLER_ID);
        manipulator = new XboxController(MANIPULATOR_CONTROLLER_ID);
    }

    /* Drive */
    public double getDriveX() {
        return deadzone(drive.leftStickX.get());
    }

    public double getDriveY() {
        return deadzone(drive.leftStickY.get());
    }

    public double getDriveRot() {
        return deadzone(drive.rightStickX.get());
    }

    public boolean getSlowMode() {
        return drive.leftShoulder.isPressed();
    }

    
    
    /* Manipulator */
    public boolean getToggleIntake() {
        return manipulator.y.leadingEdge();
    }
    
    public boolean getShoot() {
        return manipulator.a.leadingEdge();
    }
    
    public boolean getEject() {
        return manipulator.start.leadingEdge();
    }

    public boolean getAim() { // Both drive an manipulator
        return drive.rightShoulder.isPressed() || manipulator.rightShoulder.isPressed();
    }

    public boolean getAimOverride() { // Both drive and manipulator
        return drive.select.leadingEdge() || manipulator.select.leadingEdge();
    }
    
    /* Climb */
    public boolean getClimberNextStep() {
        return manipulator.x.isPressed();
    }

    public boolean getClimberPreviousStep() {
        return manipulator.b.leadingEdge();
    }


    /* Temporary */
    public boolean getFollowPath() {
        return manipulator.start.isPressed();
    }


    
    private double deadzone(double amount) {
        if (Math.abs(amount) < JOYSTICK_DEAD_ZONE.get()) {
            return 0;
        }
        return Math.signum(amount) * Utils.map(Math.abs(amount), JOYSTICK_DEAD_ZONE.get(), 1, 0, 1);
    }


    @Override
    public void robotPeriodic() {
        drive.update();
        manipulator.update();
    }
}
