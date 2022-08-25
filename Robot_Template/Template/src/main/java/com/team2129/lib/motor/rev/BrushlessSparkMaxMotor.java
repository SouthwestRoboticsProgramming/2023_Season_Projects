package com.team2129.lib.motor.rev;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2129.lib.schedule.Subsystem;

/**
 * Represents a brushless motor controlled by a Spark MAX.
 */
public final class BrushlessSparkMaxMotor extends SparkMaxMotor {
    /**
     * Creates a new instance that belongs to the specified {@code Subsystem}.
     * 
     * @param parent parent subsystem
     * @param canID CAN id of the Spark MAX
     */
    public BrushlessSparkMaxMotor(Subsystem parent, int canID) {
        super(parent, canID, MotorType.kBrushless);
    }
}
