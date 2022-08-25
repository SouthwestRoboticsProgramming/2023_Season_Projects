package com.team2129.lib.motor.ctre;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.team2129.lib.schedule.Subsystem;

/**
 * Represents a Talon FX motor.
 */
public final class TalonFXMotor extends TalonMotor {
    private static final int ENCODER_TICKS_PER_ROTATION = 2048;    

    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * This constructor assumes the motor is on the default RoboRIO CAN bus.
     * 
     * @param parent parent subsystem
     * @param canID CAN id of the motor
     */
    public TalonFXMotor(Subsystem parent, int canID) {
        this(parent, canID, "rio");
    }

    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * This constructor allows you to specify which CAN bus the motor is on.
     * 
     * @param parent parent subsystem
     * @param canID CAN id of the motor
     * @param canBus CAN bus the motor is on
     */
    public TalonFXMotor(Subsystem parent, int canID, String canBus) {
        super(parent, createConfiguredFX(canID, canBus), ENCODER_TICKS_PER_ROTATION);
    }

    private static TalonFX createConfiguredFX(int canID, String canBus) {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor;
        config.supplyCurrLimit = new SupplyCurrentLimitConfiguration(
            true, // Enabled
            35, // Continuous current limit
            70, // Peak current limit
            0.1 // Peak current limit duration
        );
        
        TalonFX fx = new TalonFX(canID, canBus);
        fx.configFactoryDefault();
        fx.configAllSettings(config);

        return fx;
    }
}
