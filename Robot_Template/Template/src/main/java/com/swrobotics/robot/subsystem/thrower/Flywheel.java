package com.swrobotics.robot.subsystem.thrower;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.BangBangVelocityCalculator;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

import com.swrobotics.robot.Constants;

public class Flywheel implements Subsystem {

    private static final NTDouble IDLE_VELOCITY = new NTDouble("Thrower/Flywheel/Idle_RPM", 750);
    private static final NTDouble MOTOR_TEMP = new NTDouble("Thrower/Flywheel/Motor_Temp", 0.0);

    private static final int FLYWHEEL_MOTOR_ID = 13;

    private final TalonFXMotor motor;
    
    public Flywheel() {
        motor = new TalonFXMotor(this, FLYWHEEL_MOTOR_ID, Constants.CANIVORE);
        motor.setNeutralMode(NeutralMode.COAST);
        
        BangBangVelocityCalculator bangCalc = new BangBangVelocityCalculator();
        bangCalc.setMultiplier(0.9); // Max output
        bangCalc.setThreshold(Angle.cwRot(50)); // Within 50 rpm
        bangCalc.setSpeedRamp(1.0); // Deceleration
        motor.setVelocityCalculator(bangCalc);

        MOTOR_TEMP.setTemporary();
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
    }

    @Override 
    public void periodic() {
        MOTOR_TEMP.set(motor.getTemperature());
    }
}
