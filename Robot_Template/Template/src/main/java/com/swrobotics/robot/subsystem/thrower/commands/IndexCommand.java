package com.swrobotics.robot.subsystem.thrower.commands;

import com.swrobotics.robot.subsystem.thrower.Hopper;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Command;

import edu.wpi.first.wpilibj.Timer;

public class IndexCommand implements Command {
    private static final NTDouble RUN_TIME = new NTDouble("Thrower/Commands/Index/Index_Time_Seconds", 1);
    private static final NTDouble RUN_SPEED = new NTDouble("Thrower/Commands/Index/Index_Speed_Percent", 0.3);

    private final Hopper hopper;
    private final Timer timer;

    public IndexCommand(Hopper hopper) {
        this.hopper = hopper;
        timer = new Timer();
        timer.start();
    }

    @Override
    public void init() {
        hopper.setOverride(true);
    }

    @Override
    public boolean run() {
        hopper.setIndexPercent(RUN_SPEED.get());;
        return timer.hasElapsed(RUN_TIME.get());
    }
    
    @Override
    public void end(boolean cancel) {
        hopper.setOverride(false);
    }
}
