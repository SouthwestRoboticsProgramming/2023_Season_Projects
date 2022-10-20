package com.swrobotics.robot.subsystem.thrower.commands;

import com.swrobotics.robot.subsystem.thrower.Hopper;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Command;

import edu.wpi.first.wpilibj.Timer;

public class ReindexCommand implements Command {
    private static final NTDouble RUN_TIME = new NTDouble("Thrower/Commands/Index/Reindex_Time_Seconds", 0.2);
    private static final NTDouble RUN_SPEED = new NTDouble("Thrower/Commands/Index/Reindex_Speed_Percent", 0.1);

    private final Hopper hopper;
    private final Timer timer;

    public ReindexCommand(Hopper hopper) {
        this.hopper = hopper;
        timer = new Timer();
        timer.start();
    }


    @Override
    public boolean run() {
        hopper.setIndexPercent(-Math.abs(RUN_SPEED.get()));
        return timer.hasElapsed(RUN_TIME.get());
    }

    @Override
    public void end(boolean wasCancelled) {
        hopper.turnOffControlOverride();
    }
    
}
