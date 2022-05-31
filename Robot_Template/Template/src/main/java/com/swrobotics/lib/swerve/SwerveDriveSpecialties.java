package com.swrobotics.lib.swerve;

/**
 * Swerve Drive Specialties module configurations.
 */
public enum SwerveDriveSpecialties {
    L1(10,20),
    L2(20,40),
    L3(30,60),
    L4(40,80); // > fast FIXME

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
        return maxWheelSpeed;
    }


}
