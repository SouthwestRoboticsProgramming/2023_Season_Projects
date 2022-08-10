package com.team2129.lib.utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

public class LazyTalonSRXConfiguration {
    public static void configureDefaultTalon(TalonSRX talon) {
        talon.set(ControlMode.PercentOutput, 0.0);

        TalonSRXConfiguration config = new TalonSRXConfiguration();
        {
            config.continuousCurrentLimit = 35;
            config.peakCurrentLimit = 60;
            config.peakCurrentDuration = 1; // Dunno if this is right
        }

        talon.configFactoryDefault(100);
        TalonErrorChecker.checkError(talon.configAllSettings(config, 100), "Failed to config enhanced default settings");
    }
}
