package com.swrobotics.bert.util;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

public final class TalonFXBuilder {
    private static final String DEFAULT_CAN_BUS = "rio";

    private final int canID;
    private final TalonFXConfiguration config;

    private String canBus = DEFAULT_CAN_BUS;
    private boolean inverted = false;

    public TalonFXBuilder(int canID) {
        this.canID = canID;

        config = new TalonFXConfiguration();
        config.neutralDeadband = 0.001; // Percent output considered neutral (Will trigger coast or brake)
        config.openloopRamp = 0.5;   // Seconds to ramp from 0% to 100% 
        config.closedloopRamp = 0.5; // Seconds to ramp from 0% to 100%
    }

    public TalonFXBuilder setCANBus(String canBus) {
        this.canBus = canBus;
        return this;
    }

    public TalonFXBuilder setPIDF(double kP, double kI, double kD, double kF) {
        SlotConfiguration slot = config.slot0;

        slot.kP = kP;
        slot.kI = kI;
        slot.kD = kD;
        slot.kF = kF;
        slot.closedLoopPeakOutput = 1;

        return this;
    }

    public TalonFXBuilder setInverted(boolean inverted) {
        this.inverted = inverted;
        return this;
    }

    public TalonFXBuilder setRamp(double ramp) {
        config.closedloopRamp = ramp;
        config.openloopRamp = ramp;
        return this;
    }

    public TalonFX build() {
        TalonFX fx = new TalonFX(canID, canBus);

        fx.configAllSettings(config);
        fx.setInverted(inverted);
        fx.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

        return fx;
    }
}
