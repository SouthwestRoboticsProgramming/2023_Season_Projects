package com.swrobotics.robot.subsystem.climber;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.MathUtil;
import com.swrobotics.lib.net.NTDouble;

public class ClimberState {
    private final NTDouble telePercent;
    private final NTDouble rotAngle;
    private final boolean loaded;

    public ClimberState(NTDouble telePercent, NTDouble rotAngle, boolean loaded) {
        this.telePercent = telePercent;
        this.rotAngle = rotAngle;
        this.loaded = loaded;
    }

    public double getTelePercent() {
        return MathUtil.clamp(telePercent.get(), 0.0, 1.0);
    }

    public Angle getRotAngle() {
        return Angle.cwDeg(rotAngle.get());
    }

    public boolean getLoaded() {
        return loaded;
    }
}
