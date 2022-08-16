package com.swrobotics.robot.subsystem.thrower;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.TalonMotor;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.net.NTUtils;
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

    private final TalonMotor motor;
    private Runnable motorMode;
    
    public Flywheel() {
        TalonFX talon_toWrap = new TalonFX(FLYWHEEL_MOTOR_ID, Constants.CANIVORE);
        talon_toWrap.configFactoryDefault();

        TalonFXConfiguration config = new TalonFXConfiguration();
        {
            config.supplyCurrLimit = new SupplyCurrentLimitConfiguration(
                true,
                35, // Continuous current limit
                70, // Peak current limit
                0.1 // Peak current limit duration
            );
        }

        talon_toWrap.configAllSettings(config);

        talon_toWrap.setNeutralMode(NeutralMode.Coast);
        talon_toWrap.setInverted(false);

        motor = new TalonMotor(this, talon_toWrap);
        motor.setPIDController(NTUtils.makeAutoTunedPID(KP, KI, KD));
        motor.setFeedforward(new SimpleMotorFeedforward(KS.get(), KV.get()));
        motor.setFlywheelMode(FLYWHEEL_MODE.get());

        motor.getEncoder().

        motorMode = () -> motor.percent(0);

        // Update feedforward
        KS.onChange(() -> motor.setFeedforward(new SimpleMotorFeedforward(KS.get(), KV.get())));
        KV.onChange(() -> motor.setFeedforward(new SimpleMotorFeedforward(KS.get(), KV.get())));
    }

    /**
     * 
     * @param velocity Angle/Second
     */
    public void setFlywheelVelocity(Angle velocity) {
        motorMode = () -> motor.velocity(velocity);
    }

    public void idle() {
        motorMode = () -> motor.velocity(Angle.cwRot(IDLE_VELOCITY.get()));
    }

    public void stop() {
        motorMode = () -> motor.percent(0);
        System.out.println("Stop");
    }

    @Override 
    public void periodic() {
        // TODO: Log temperature, speed, etc...
       // motorMode.run();
       motor.velocity(Angle.cwDeg(100));
    }
}
