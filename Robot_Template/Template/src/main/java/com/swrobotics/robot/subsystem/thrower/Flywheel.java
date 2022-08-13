package com.swrobotics.robot.subsystem.thrower;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.TalonMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.net.NTUtils;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.utils.LazyTalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import static com.swrobotics.robot.Constants.*;

public class Flywheel implements Subsystem { // TODO: Why was the old one final?
    private static final NTDouble KP = new NTDouble("Thrower/Flywheel/kP", 0);
    private static final NTDouble KI = new NTDouble("Thrower/Flywheel/kI", 0);
    private static final NTDouble KD = new NTDouble("Thrower/Flywheel/kD", 0);

    private static final NTDouble IDLE_VELOCITY = new NTDouble("Thrower/Flywheel/Idle_RPM", 750);

    private static final int FLYWHEEL_MOTOR_ID = 30;

    private final TalonMotor motor;

    private Angle velocity = Angle.cwDeg(0);
    
    public Flywheel() {
        TalonFX talon_toWrap = new TalonFX(FLYWHEEL_MOTOR_ID, CANIVORE);
        LazyTalonFXConfiguration.configureDefaultTalon(talon_toWrap); // TODO: Be less lazy
        talon_toWrap.setNeutralMode(NeutralMode.Coast);
        talon_toWrap.setInverted(true);

        motor = new TalonMotor(this, talon_toWrap);
        motor.setPIDController(NTUtils.makeAutoTunedPID(KP, KI, KD));
        motor.setFlywheelMode(true);
        // TODO: IMPORTANT: Configure and tune a feedforward controller
    }

    public void setFlywheelVelocity(Angle velocity) {
        this.velocity = velocity;
    }

    public void idle() {
        velocity = Angle.cwRot(IDLE_VELOCITY.get());
    }

    @Override 
    public void periodic() {
        // TODO: Log temperature, speed, etc...
        motor.velocity(velocity);
    }
}
