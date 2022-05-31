package com.swrobotics.lib.swerve;

public enum SwerveDriveSpecialties {
    L1(10),
    L2(20),
    L3(30),
    L4(40); // > fast FIXME

    private final double gearRatio;
    
    SwerveDriveSpecialties(double gearRatio) {
        this.gearRatio = gearRatio;
    }

    public double getGearRatio() {
        return gearRatio;
    }


}
