package com.team2129.lib.motor.ctre;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.utils.TalonErrorChecker;

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

    // TODO: Explain default creation
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

    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * This constructor allows you to build a TalonFXMotor using a TalonFX that you have already configured with custom settings.
     * 
     * @param parent Parent subsystem
     * @param configuredTalon TalonFX already configured to fit your needs.
     */
    public TalonFXMotor(Subsystem parent, TalonFX configuredTalon) {
        super(parent, configuredTalon, ENCODER_TICKS_PER_ROTATION);
    }

    private static TalonFX createConfiguredFX(int canID, String canBus) {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor;
        config.initializationStrategy = SensorInitializationStrategy.BootToZero;
        config.supplyCurrLimit = new SupplyCurrentLimitConfiguration(
            true, // Enabled
            35, // Continuous current limit
            60, // Peak current limit
            0.1 // Peak current limit duration
        );
        
        TalonFX fx = new TalonFX(canID, canBus);
        fx.configFactoryDefault(100);
        TalonErrorChecker.checkError(fx.configAllSettings(config, 100), "Failed to configure enhanced default TalonFX");

        return fx;
    }
}
