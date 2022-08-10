package com.team2129.lib.utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;

public class LazyTalonFXConfiguration {
    public static void configureDefaultTalon(TalonFX talon) {
        talon.set(ControlMode.PercentOutput, 0.0);

        TalonFXConfiguration config = new TalonFXConfiguration();
        {
            SupplyCurrentLimitConfiguration supplyLimit = new SupplyCurrentLimitConfiguration(
                true,
                35, // Continuous current limit
                60, // Peak current limit
                0.1 // Peak current limit duration
            );
        
            config.initializationStrategy = SensorInitializationStrategy.BootToZero;
            config.supplyCurrLimit = supplyLimit;
        }

        talon.configFactoryDefault(100);
        TalonErrorChecker.checkError(talon.configAllSettings(config, 100), "Failed to config enhanced default settings");
    }
}
