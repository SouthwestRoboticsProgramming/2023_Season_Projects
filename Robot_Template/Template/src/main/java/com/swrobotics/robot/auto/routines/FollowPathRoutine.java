package com.swrobotics.robot.auto.routines;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.team2129.lib.math.Angle;
import com.team2129.lib.routine.Routine;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.Timer;

public class FollowPathRoutine extends Routine {
    private final Timer timer;
    private final HolonomicDriveController follower; // Lets swerve drive follow a path

    private final Trajectory trajectory;
    
    private final Supplier<Pose2d> currentPose;
    private final Supplier<Angle> desiredAngle;
    
    private final Consumer<ChassisSpeeds> outputChassisSpeeds;

    public FollowPathRoutine(
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
    public void periodic() {
        double currentTime = timer.get();
        State desiredChassisState = trajectory.sample(currentTime);
        Angle desiredRotation = desiredAngle.get();

        ChassisSpeeds targetChassisSpeeds = follower.calculate(currentPose.get(), desiredChassisState, desiredRotation.toRotation2dCW());

        // Output
        outputChassisSpeeds.accept(targetChassisSpeeds);
    }

    @Override
    public void end() {
        timer.stop();
        outputChassisSpeeds.accept(new ChassisSpeeds());
    }

    public Pose2d getInitialPose() {
        return trajectory.getInitialPose();
    }

    // TODO: End early
}
