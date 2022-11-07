package com.swrobotics.robot.subsystem.thrower.commands;

import com.swrobotics.lib.schedule.CommandSequence;
import com.swrobotics.robot.subsystem.thrower.Hopper;

public class ShootCommand extends CommandSequence {
    public ShootCommand(Hopper hopper) {

        append(new IndexCommand(hopper));
        append(new ReindexCommand(hopper));
    }
}
