package com.swrobotics.lib.motor;

public enum MotorType {
    Falcon500(6380, 1000),
    NEO(5880, 1000),
    NEO_550(11710, 1000),
    Seven75_Pro(18730, 1000),
    Seven75_RedLine(19500, 1000),
    CIM(5330, 1000),
    MiniCIM(5840, 1000),
    BAG(13180, 1000),
    AM_9015(14270, 1000),
    BaneBots_550(19300, 1000),
    NeveRest(5480, 1000),
    Snowblower(100, 1000);

    private final double maxRPM;
    private final double maxAccel;

    MotorType(double maxRPM, double maxAccel) {
        this.maxRPM = maxRPM;
        this.maxAccel = maxAccel;
    }

    public double getMaxRPM() {
        return maxRPM;
    }

    public double getAcceleration() {
        return maxAccel;
    }
}
