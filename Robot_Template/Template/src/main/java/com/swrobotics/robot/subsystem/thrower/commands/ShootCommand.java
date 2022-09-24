package com.swrobotics.robot.subsystem.thrower.commands;

import com.swrobotics.robot.subsystem.thrower.Hopper;
import com.team2129.lib.schedule.CommandSequence;

public class ShootCommand extends CommandSequence {
    public ShootCommand(Hopper hopper) {
        System.out.println("Shoot");
        append(new IndexCommand(hopper));
        append(new ReindexCommand(hopper));
    }
}
