package com.swrobotics.robot.input;

import edu.wpi.first.wpilibj.XboxController;

public class Input {

    private final XboxController controller;
    
    public Input() {
        controller = new XboxController(0);
    }

    public double getExample() { // This is how input functions are usually formatted
        return controller.getLeftTriggerAxis();
    }

}
