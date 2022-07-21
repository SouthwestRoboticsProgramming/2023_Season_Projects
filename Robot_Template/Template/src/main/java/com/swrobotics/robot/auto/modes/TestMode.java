package com.swrobotics.robot.auto.modes;

import java.util.function.Consumer;

import com.swrobotics.robot.auto.TrajectoryReader;
import com.swrobotics.robot.auto.routines.FollowPathRoutine;
import com.team2129.lib.drivers.swerve.SwerveDrive;
import com.team2129.lib.math.Angle;
import com.team2129.lib.routine.Routine;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class TestMode extends Routine {
    

    private final String path = "paths/test.path";

    private final FollowPathRoutine followRoutine;

    public TestMode(SwerveDrive swerve, Consumer<ChassisSpeeds> outputSpeeds) {
        ProfiledPIDController angleController = new ProfiledPIDController(5, 0, 0, new TrapezoidProfile.Constraints(1.2 * Math.PI, Math.pow(1.2 * Math.PI , 2)));

        angleController.enableContinuousInput(-Math.PI, Math.PI);

        TrajectoryConfig config = new TrajectoryConfig(2.2, 2.3);
        config.setKinematics(swerve.getKinematics());
        // config.setStartVelocity(0);
        // config.setEndVelocity(0);

        // Generate routine from PathWeaver
        Trajectory trajectory = TrajectoryReader.generateTrajectoryFromFile(path, config);

        followRoutine = new FollowPathRoutine(trajectory, 
        new PIDController(1, 0, 0), 
        new PIDController(1, 0, 0), 
        angleController, 
        () -> Angle.cwDeg(0), 
        swerve::getPose, 
        outputSpeeds);
    }
}
