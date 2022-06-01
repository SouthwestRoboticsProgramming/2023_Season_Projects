package com.swrobotics.lib.swerve;

public interface SwerveModuleSpecs {

    /**
     * Get the gear ratio of the module configuration.
     * @return Gear ratio in X:1.
     */
    public double getGearRatio();

    /**
     * Get the max attainable speed of the module with the motor at 100% power.
     * @return The max velocity of the wheel in m/s.
     */
    public double getMaxWheelSpeed();
}
