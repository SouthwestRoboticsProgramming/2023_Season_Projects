package com.swrobotics.robot.subsystem.thrower;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.BangBangCalculator;
import com.team2129.lib.motor.calc.CompoundVelocityCalculator;
import com.team2129.lib.motor.calc.FeedForwardVelocityCalculator;
import com.team2129.lib.motor.calc.PIDCalculator;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

import com.swrobotics.robot.Constants;

public final class Flywheel implements Subsystem {

    private static final NTDouble IDLE_VELOCITY = new NTDouble("Thrower/Flywheel/Idle_RPS", 10);
    private static final NTDouble L_MOTOR_TEMP = new NTDouble("Thrower/Flywheel/Motor_Temp", 2129);
    private static final NTDouble L_MOTOR_VELOCITY = new NTDouble("Thrower/Flywheel/Velocity RPS", 2129);

    private static final NTDouble KP = new NTDouble("Thrower/Flywheel/kP", 0.0003);
    private static final NTDouble KI = new NTDouble("Thrower/Flywheel/kI (Caution)", 0.0);
    private static final NTDouble KD = new NTDouble("Thrower/Flywheel/kD (Heavy Caution)", 0.0);
    private static final int FLYWHEEL_MOTOR_ID = 13;

    private static final NTDouble KS = new NTDouble("Thrower/Flywheel/kS", 0.0);
    private static final NTDouble KV = new NTDouble("Thrower/Flywheel/kV", 0.00003); // Change to adjust potency of velocity maintenance.
    
    private static final Angle BANG_THRESH_LOW = Angle.cwDeg(500);
    private static final Angle BANG_THRESH_HIGH = Angle.cwDeg(-200);

    private final TalonFXMotor motor;
    
    public Flywheel() {
        motor = new TalonFXMotor(this, FLYWHEEL_MOTOR_ID, Constants.CANIVORE);
        motor.setNeutralMode(NeutralMode.COAST); // DO NOT CHANGE

        /*
         * General control scheme:
         * 1. Bang bang: Get flywheel close to velocity as quickly as mechanically possible.
         * 2. Feedforward: Maintain flywheel velocity (Takes load off PID tuning).
         * 3. PID: Get flywheel exactly to setpoint (Just KP, keep it low enough not to vibrate the belts)
         * 
         * Tuning Guide:
         * Bang bang: Increase threshold's distance from zero if the flywheel initially overshoots setpoint,
         *            Add multiplier if spin-up is too violent.
         * 
         * Feedforward: Tune while idling, Should keep flywheel at a stable velocity (listen for the sound of a car car starting),
         *              Check that KV still works at high velocity (set idle velocity to what you want to test and comment out PID),
         *              This can potentially be removed if too sensitive and replaced with high PID constants.
         * 
         * PID: Leave KD alone, only tweak KI if desperate,
         *      KP should get motor to hover around the setpoint without revving.
         */
        
        BangBangCalculator bangCalc = new BangBangCalculator();

        FeedForwardVelocityCalculator feedCalc = new FeedForwardVelocityCalculator(KS, KV);

        PIDCalculator pidCalc = new PIDCalculator(KP, KI, KD);
        pidCalc.allowNegativeOutputs(false);

        bangCalc.setLowerThreshold(BANG_THRESH_LOW);
        bangCalc.setUpperThreshold(BANG_THRESH_HIGH);


        motor.setVelocityCalculator(new CompoundVelocityCalculator(pidCalc, feedCalc, bangCalc));

        L_MOTOR_TEMP.setTemporary();
        L_MOTOR_VELOCITY.setTemporary();
    }

    /**
     *
     * @param velocity Angle/Second
     */
    public void setFlywheelVelocity(Angle velocity) {
        motor.velocity(velocity);
    }

    public void idle() {
        // motor.velocity(Angle.cwRot(IDLE_VELOCITY.get()));
        motor.stop();
    }

    public void stop() {
        motor.stop();
    }

    @Override
    public void periodic() {
        L_MOTOR_TEMP.set(motor.getTemperature());
        L_MOTOR_VELOCITY.set(motor.getEncoder().getVelocity().getCWDeg() / 360);
    }
}
