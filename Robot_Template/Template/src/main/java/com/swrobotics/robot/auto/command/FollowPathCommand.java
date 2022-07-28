package com.swrobotics.robot.auto.command;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.team2129.lib.math.Angle;

import com.team2129.lib.schedule.command.Command;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.Timer;

public class FollowPathCommand implements Command {
    private final Timer timer;
    private final HolonomicDriveController follower; // Lets swerve drive follow a path

    private final Trajectory trajectory;
    
    private final Supplier<Pose2d> currentPose;
    private final Supplier<Angle> desiredAngle;
    
    private final Consumer<ChassisSpeeds> outputChassisSpeeds;

    public FollowPathCommand(
        Trajectory trajectory,

        PIDController xController,
        PIDController yController,
        ProfiledPIDController angleController,

        Supplier<Angle> desiredAngle,
        Supplier<Pose2d> currentPose,

        Consumer<ChassisSpeeds> outputChassisSpeeds
    ) {
        
        follower = new HolonomicDriveController(xController, yController, angleController);

        this.trajectory = trajectory;
        
        this.desiredAngle = desiredAngle;
        this.outputChassisSpeeds = outputChassisSpeeds;
        this.currentPose = currentPose;

        timer = new Timer();
    }

    @Override
    public void init() {
        timer.reset();
        timer.start();
    }

    @Override
    public boolean run() {
        double currentTime = timer.get();
        State desiredChassisState = trajectory.sample(currentTime);
        Angle desiredRotation = desiredAngle.get();

        ChassisSpeeds targetChassisSpeeds = follower.calculate(currentPose.get(), desiredChassisState, desiredRotation.toRotation2dCW());

        // Output
        outputChassisSpeeds.accept(targetChassisSpeeds);

        // TODO-Mason: Return true when path is done
        return false;
    }

    @Override
    public void end(boolean cancelled) {
        timer.stop();
        outputChassisSpeeds.accept(new ChassisSpeeds());
    }

    public Pose2d getInitialPose() {
        return trajectory.getInitialPose();
    }
}
