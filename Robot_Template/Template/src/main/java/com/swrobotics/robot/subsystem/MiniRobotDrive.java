package com.swrobotics.robot.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.swrobotics.lib.motor.MotorMode;
import com.swrobotics.lib.motor.TalonMotor;
import com.swrobotics.lib.motor.TalonMotorBuilder;
import com.swrobotics.lib.routine.Routine;
import com.swrobotics.robot.input.Input;

public class MiniRobotDrive extends Routine {
    private final Input input;
    private final TalonMotor leftMotor, rightMotor;

    public MiniRobotDrive(Input input) {
        this.input = input;
        TalonSRX leftSRX = new TalonSRX(1);
        TalonSRX rightSRX = new TalonSRX(2);

        leftMotor = new TalonMotorBuilder(leftSRX, 178.9)
            .build();

        rightMotor = new TalonMotorBuilder(rightSRX, 178.9)
            .build();
    }

    @Override
    public void periodic() {
        leftMotor.set(MotorMode.PERCENT_OUT, 1);
        rightMotor.set(MotorMode.PERCENT_OUT, 1);
    }
}
