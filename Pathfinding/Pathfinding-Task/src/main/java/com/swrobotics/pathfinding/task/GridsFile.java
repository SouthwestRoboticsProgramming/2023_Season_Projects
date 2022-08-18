package com.swrobotics.pathfinding.task;

import com.swrobotics.pathfinding.geom.Circle;
import com.swrobotics.pathfinding.geom.RobotShape;
import com.swrobotics.pathfinding.grid.Grid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public final class GridsFile {
    private RobotShape robot;
    private List<Grid> grids;

    private GridsFile() {}

    public GridsFile(RobotShape robot, List<Grid> grids) {
        this.robot = robot;
        this.grids = grids;
    }

    public static GridsFile load(File file) {
        try {
            return Grid.GSON.fromJson(new FileReader(file), GridsFile.class);
        } catch (FileNotFoundException e) {
            GridsFile def = new GridsFile();
            def.robot = new Circle(0, 0, 0.5);
            def.grids = new ArrayList<>();

            System.err.println("Grids file not found, saving default");
            def.save(file);

            return def;
        }
    }

    public void save(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            Grid.GSON.toJson(this, writer);
            writer.close();
        } catch (Exception e2) {
            System.err.println("Failed to save grids file");
            e2.printStackTrace();
        }
    }

    public RobotShape getRobot() {
        return robot;
    }

    public void setRobot(RobotShape robot) {
        this.robot = robot;
    }

    public List<Grid> getGrids() {
        return grids;
    }

    public void setGrids(List<Grid> grids) {
        this.grids = grids;
    }
}
