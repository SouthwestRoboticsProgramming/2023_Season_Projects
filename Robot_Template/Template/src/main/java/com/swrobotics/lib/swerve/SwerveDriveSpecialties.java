package com.swrobotics.lib.swerve;

/**
 * Swerve Drive Specialties MK4 module configurations.
 */
public enum SwerveDriveSpecialties {
    L1_Falcon(8.14, 13.5),
    L2_Falcon(6.75, 16.3),
    L3_Falcon(6.12, 18.0),
    L4_Falcon(5.14, 21.4),

    L1_Neo(8.14, 12.0),
    L2_Neo(6.75, 14.5),
    L3_Neo(6.12, 16.0),
    L4_Neo(5.14, 19.0);



    private final double gearRatio;
    private final double maxWheelSpeed;
    
    SwerveDriveSpecialties(double gearRatio, double maxWheelSpeed) {
        this.gearRatio = gearRatio;
        this.maxWheelSpeed = maxWheelSpeed;
    }

    /**
     * Get the gear ratio of the module configuration.
     * @return Gear ratio in X:1.
     */
    public double getGearRatio() {
        return gearRatio;
    }

    /**
     * Get the max attainable speed of the module with the motor at 100% power.
     * @return The max velocity of the wheel in m/s.
     */
    public double getMaxWheelSpeed() {
        return maxWheelSpeed / 3.28084; // Convert from ft/s to m/s
    }


}
