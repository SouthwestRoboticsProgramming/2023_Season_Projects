package com.swrobotics.bert;

import edu.wpi.first.wpilibj.RobotBase;

public final class Main {
    public static void main(String[] args) {
        RobotBase.startRobot(Robot::get);

        // File file = new File("/home/lvuser/networktables.ini");
        // file.delete();

        // System.out.println("Deleted it");
    }

    private Main() {
        throw new AssertionError();
    }
}
