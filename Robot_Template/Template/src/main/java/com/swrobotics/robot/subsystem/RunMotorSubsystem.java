package com.swrobotics.robot.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.encoder.TalonInternalEncoder;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.Motor;
import com.swrobotics.lib.motor.MotorMode;
import com.swrobotics.lib.motor.TalonMotorBuilder;
import com.swrobotics.lib.motor.implementations.VictorSPMotor;
import com.swrobotics.lib.routine.Routine;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public final class RunMotorSubsystem extends Routine {
    // private final Motor motor;
    // private final Encoder encoder;
    private final VictorSP victor;

    public RunMotorSubsystem() {
        victor = new VictorSP(0);
        // motor = new VictorSPMotor(victor);

        // encoder = motor.getInternalEncoder(177.6);

        // motor.assignEncoder(encoder);
        // encoder.zeroAngle();
    }
    
    @Override
    public void periodic() {
        double target = 180;//180 + 180 * Math.sin((System.currentTimeMillis() % 1000) / 1000.0 * Math.PI);

        // TODO: Warning when using mode that requires PID, but no pid
        // motor.set(MotorMode.ANGLE, Angle.cwDeg(target));
        // motor.set(MotorMode.PERCENT_OUT, 1.0);
        System.out.println("Hello :)");
        victor.set(1);

        // System.out.println("At " + encoder.getAngle().getCWDeg() + ", targeting " + target);
    }
}
