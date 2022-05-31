package com.swrobotics.lib.swerve;

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

    public double getGearRatio() {
        return gearRatio;
    }

    public double getMaxWheelSpeed() {
        return maxWheelSpeed;
    }


}
