package com.swrobotics.robot.subsystem.thrower.commands;

import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.robot.subsystem.thrower.Hopper;

import edu.wpi.first.wpilibj.Timer;

public class IndexCommand implements Command {
    private static final NTDouble RUN_TIME = new NTDouble("Thrower/Commands/Index/Index_Time_Seconds", 0.25);
    private static final NTDouble RUN_SPEED = new NTDouble("Thrower/Commands/Index/Index_Speed_Percent", 0.3);

    private final Hopper hopper;
    private Timer timer;

    public IndexCommand(Hopper hopper) {
        this.hopper = hopper;
    }

    @Override
    public void init() {
        timer = new Timer();
        timer.start();
    }

    @Override
    public boolean run() {
        hopper.setIndexPercent(RUN_SPEED.get());;
        return timer.hasElapsed(RUN_TIME.get());
    }

    @Override
    public void end(boolean wasCancelled) {
        hopper.turnOffControlOverride();
    }
    
}
