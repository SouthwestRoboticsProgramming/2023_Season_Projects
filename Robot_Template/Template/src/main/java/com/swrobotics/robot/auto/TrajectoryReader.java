package com.swrobotics.robot.auto;

import java.io.IOException;
import java.nio.file.Path;

import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;

/* FROM FRC 1678 */

public class TrajectoryReader {

    public static Trajectory generateTrajectoryFromFile(String filePath, TrajectoryConfig config) {
        try {
            Path path = Filesystem.getDeployDirectory().toPath().resolve(filePath);
            TrajectoryGenerator.ControlVectorList controlVectors = WaypointReader.getControlVectors(path);

            return TrajectoryGenerator.generateTrajectory(controlVectors, config);
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory path : " + filePath, ex.getStackTrace());
            return null;
        }
    }
}
