package com.swrobotics.robot.subsystem;

import com.swrobotics.robot.Robot;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.wpilib.RobotState;

public class Localization implements Subsystem {

    private static final NTBoolean USE_LIMELIGHT = new NTBoolean("Limelight/Use for localization", true);

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

        // Update position using limelight
        // Only update if it is accurate, the robot is in teleop, and we actually want to use it.
        if (limelight.isAccurate() && USE_LIMELIGHT.get() && Robot.get().getCurrentState() == RobotState.TELEOP) {
            
        }
    }

    
}
