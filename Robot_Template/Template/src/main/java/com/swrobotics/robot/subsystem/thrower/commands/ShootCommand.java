package com.swrobotics.robot.subsystem.thrower.commands;

import com.swrobotics.robot.subsystem.thrower.Hopper;
import com.team2129.lib.schedule.CommandSequence;

public class ShootCommand extends CommandSequence {
    // TODO: Is reapeat count necessary?
    
    public ShootCommand(Hopper hopper) {

        append(new IndexCommand(hopper));
        append(new ReindexCommand(hopper));
    }
}
