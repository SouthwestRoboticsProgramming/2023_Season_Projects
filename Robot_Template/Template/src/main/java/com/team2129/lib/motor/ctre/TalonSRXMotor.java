package com.team2129.lib.motor.ctre;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.utils.TalonErrorChecker;

/**
 * Represents a motor controlled by a Talon SRX motor controller.
 */
public final class TalonSRXMotor extends TalonMotor {
    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * Use this constructor if no encoder is connected to the SRX.
     * 
     * @param parent parent subsystem
     * @param canID CAN id of the SRX
     */
    public TalonSRXMotor(Subsystem parent, int canID) {
        this(parent, canID, -1);
    }

    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * Use this constructor if an encoder is connected to the SRX.
     * 
     * @param parent parent subsystem
     * @param canID CAN id of the SRX
     * @param encoderTicksPerRotation encoder ticks per rotation of the motor's output shaft
     */
    public TalonSRXMotor(Subsystem parent, int canID, int encoderTicksPerRotation) {
        super(parent, createConfiguredSRX(canID), encoderTicksPerRotation);
    }

    /**
     * Create a new instance that converts from the legacy TalonSRX type.
     * Use this constructor if no encoder is connected to the SRX.
     * @param parent
     * @param configuredTalon
     */
    public TalonSRXMotor(Subsystem parent, TalonSRX configuredTalon) {
        super(parent, configuredTalon, -1);
    }

    /**
     * Create a new instance that converts from the legacy TalonSRX type.
     * Use this constructor if an encoder is connected to the SRX.
     * @param parent
     * @param configuredTalon
     * @param encoderTicksPerRotation
     */
    public TalonSRXMotor(Subsystem parent, TalonSRX configuredTalon, int encoderTicksPerRotation) {
        super(parent, configuredTalon, encoderTicksPerRotation);
    }

    private static TalonSRX createConfiguredSRX(int canID) {
        TalonSRXConfiguration config = new TalonSRXConfiguration();
        config.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor;

        TalonSRX srx = new TalonSRX(canID);
        srx.configFactoryDefault(100);
        TalonErrorChecker.checkError(srx.configAllSettings(config, 100), "Failed to configure enhanced default TalonSRX");

        // Config current limit with 100 ms timeout.
        srx.configContinuousCurrentLimit(35, 100);
        srx.configPeakCurrentLimit(60, 100);
        srx.configPeakCurrentDuration(100, 100);

        return srx;
    }
}
