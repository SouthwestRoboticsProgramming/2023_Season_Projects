package com.swrobotics.robot.subsystem.thrower;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.BangBangVelocityCalculator;
import com.team2129.lib.motor.calc.PIDFFVelocityCalculator;
import com.team2129.lib.motor.calc.PIDVelocityCalculator;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.swrobotics.robot.Constants;

public class Flywheel implements Subsystem {
    private static final NTDouble KP = new NTDouble("Thrower/Flywheel/kP", 0);
    private static final NTDouble KI = new NTDouble("Thrower/Flywheel/kI", 0);
    private static final NTDouble KD = new NTDouble("Thrower/Flywheel/kD", 0);

    private static final NTBoolean FLYWHEEL_MODE = new NTBoolean("Thrower/Flywheel/Use_FEED_not_PID", true);

    private static final NTDouble KS = new NTDouble("Thrower/Flywheel/kS", 0);
    private static final NTDouble KV = new NTDouble("Thrower/Flywheel/kV", 0);

    private static final NTDouble IDLE_VELOCITY = new NTDouble("Thrower/Flywheel/Idle_RPM", 750);

    private static final int FLYWHEEL_MOTOR_ID = 13;

    private final TalonFXMotor motor;
    
    public Flywheel() {
        motor = new TalonFXMotor(this, FLYWHEEL_MOTOR_ID, Constants.CANIVORE);
        
        updateFlywheelMode();
        FLYWHEEL_MODE.onChange(this::updateFlywheelMode);
    }

    private void updateFlywheelMode() {
        if (FLYWHEEL_MODE.get()) {
            motor.setVelocityCalculator(new BangBangVelocityCalculator());
        } else {
            motor.setVelocityCalculator(new PIDFFVelocityCalculator(KP, KI, KD, KS, KV));
        }
    }

    /**
     * 
     * @param velocity Angle/Second
     */
    public void setFlywheelVelocity(Angle velocity) {
        motor.velocity(velocity);
    }

    public void idle() {
         motor.velocity(Angle.cwRot(IDLE_VELOCITY.get()));
    }

    public void stop() {
        motor.stop();
        System.out.println("Stop");
    }

    @Override 
    public void periodic() {
        // TODO: Log temperature, speed, etc...
       motor.velocity(Angle.cwDeg(100));
    }
}
