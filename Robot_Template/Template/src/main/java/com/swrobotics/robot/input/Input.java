package com.swrobotics.robot.input;

import com.swrobotics.lib.math.Angle;

import edu.wpi.first.wpilibj.XboxController;

public class Input {

    private final XboxController controller;
    
    public Input() {
        controller = new XboxController(0);
    }

    public double getTestPercent() {
        return controller.getRightTriggerAxis();
    }

    public boolean getTestStop() {
        return controller.getPOV() == 0;
    }

    public boolean getTestHalt() {
        return controller.getPOV() == 1;
    }

    public boolean getTestHold() {
        return controller.getPOV() == 2;
    }


    // Swerve test 
    public double getSwerveX() {
        return controller.getLeftX();
    }

    public double getSwerveY() {
        return controller.getLeftY();
    }

    /**
     * Percentage of maximum clockwise rotation.
     * @return Percent
     */
    public double getSwerveRotation() {
        return controller.getRightX();
    }
}
