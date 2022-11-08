package com.swrobotics.robot.auto;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.schedule.CommandSequence;
import com.swrobotics.lib.schedule.WaitCommand;
import com.swrobotics.lib.time.Duration;
import com.swrobotics.lib.time.TimeUnit;
import com.swrobotics.robot.subsystem.drive.Drive;
import com.swrobotics.robot.subsystem.thrower.Hopper;
import com.swrobotics.robot.subsystem.thrower.commands.IndexCommand;
import com.swrobotics.robot.subsystem.thrower.commands.ShootCommand;

public final class AutoSequence extends CommandSequence {
    public AutoSequence(Drive drive, Hopper hopper) {
//        append(new AutoDriveForTime(
//            drive,
//            new DriveAutoInput(
//                new Vec2d(0, -2),
//                Angle.zero(),
//                false // Robot relative
//            ), new Duration(2.5, TimeUnit.SECONDS)
//        ));
//        append(new WaitCommand(1, TimeUnit.SECONDS));
//        append(() -> {
//            System.out.println("SHOOTING IT");
//            return true;
//        });
//        append(new ShootCommand(hopper));
//        append(() -> {
//            System.out.println("DONE");
//            return true;
//        });
    }
}
