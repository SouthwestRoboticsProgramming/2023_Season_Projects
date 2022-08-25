package com.team2129.lib.motor.ctre;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2129.lib.schedule.Subsystem;

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
        super(parent, new TalonSRX(canID), encoderTicksPerRotation);
    }
}
