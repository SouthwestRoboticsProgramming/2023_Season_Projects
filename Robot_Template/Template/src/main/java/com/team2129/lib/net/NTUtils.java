package com.team2129.lib.net;

import com.team2129.lib.wpilib.AbstractRobot;

import edu.wpi.first.math.controller.PIDController;

public final class NTUtils {
    public static PIDController makeAutoTunedPID(NTDouble kP, NTDouble kI, NTDouble kD) {
        PIDController pid = new PIDController(kP.get(), kI.get(), kD.get(), 1 / AbstractRobot.get().getPeriodicPerSecond());
        kP.onChange(() -> pid.setP(kP.get()));
        kI.onChange(() -> pid.setI(kI.get()));
        kD.onChange(() -> pid.setD(kD.get()));
        return pid;
    }
}
