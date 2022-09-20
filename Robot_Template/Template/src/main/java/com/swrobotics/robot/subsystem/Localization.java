package com.swrobotics.robot.subsystem;

import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

public class Localization implements Subsystem {

    private final Limelight limelight;

    private Vec2d position;
    private Angle angle;

    public Localization() {
        limelight = new Limelight();

        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, limelight);
    }

    @Override
    public void periodic() {
        if (limelight.isAccurate()) {
            // TODO
        }
    }

    
}
