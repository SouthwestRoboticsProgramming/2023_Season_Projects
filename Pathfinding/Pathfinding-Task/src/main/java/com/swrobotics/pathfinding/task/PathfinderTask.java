package com.swrobotics.pathfinding.task;

import com.swrobotics.messenger.client.MessageBuilder;
import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.pathfinding.Field;
import com.swrobotics.pathfinding.Point;
import com.swrobotics.pathfinding.finder.Pathfinder;
import com.swrobotics.pathfinding.geom.Circle;
import com.swrobotics.pathfinding.grid.BitfieldGrid;
import com.swrobotics.pathfinding.grid.GridUnion;
import com.swrobotics.pathfinding.grid.ShapeGrid;

import java.io.File;
import java.util.BitSet;
import java.util.List;

public final class PathfinderTask {
    private static final File CONFIG_FILE = new File("config.json");

    // Main API
    private static final String MSG_SET_POS  = "Pathfinder:SetPos";
    private static final String MSG_SET_GOAL = "Pathfinder:SetGoal";
    private static final String MSG_PATH = "Pathfinder:Path";

    // ShuffleLog API
    private static final String MSG_GET_FIELD_INFO = "Pathfinder:GetFieldInfo";
    private static final String MSG_GET_GRIDS = "Pathfinder:GetGrids";
    private static final String MSG_GET_CELL_DATA = "Pathfinder:GetCellData";
    private static final String MSG_ADD_GRID = "Pathfinder:AddGrid";
    private static final String MSG_REMOVE_GRID = "Pathfinder:RemoveGrid";
    private static final String MSG_ADD_SHAPE = "Pathfinder:AddShape";
    private static final String MSG_REMOVE_SHAPE = "Pathfinder:RemoveShape";

    private static final String MSG_FIELD_INFO = "Pathfinder:FieldInfo";
    private static final String MSG_GRIDS = "Pathfinder:Grids";
    private static final String MSG_CELL_DATA = "Pathfinder:CellData";

    private final MessengerClient msg;
    private final Field field;

    private final GridUnion grids;
    private final Pathfinder pathfinder;

    public PathfinderTask() {
        PathfinderConfigFile config = PathfinderConfigFile.load(CONFIG_FILE);
        msg = config.getMessenger().createClient();
        field = config.getField().createField();

        grids = new GridUnion(field.getCellsX(), field.getCellsY());
        // TODO: Load grids from grid file

        // Test grids for Messenger testing
        BitfieldGrid bitGrid = new BitfieldGrid(field.getCellsX(), field.getCellsY());
        ShapeGrid shapeGrid = new ShapeGrid(field.getCellsX(), field.getCellsY(), field, new Circle(0, 0, 0.5));
        shapeGrid.addShape(new Circle(0, 3.5, 1));
        shapeGrid.addShape(new Circle(0, -3.5, 1));

        grids.addGrid(bitGrid);
        grids.addGrid(shapeGrid);

        pathfinder = config.getFinderType().create(grids);

        msg.addHandler(MSG_SET_POS, this::onSetPos);
        msg.addHandler(MSG_SET_GOAL, this::onSetGoal);

        msg.addHandler(MSG_GET_FIELD_INFO, this::onGetFieldInfo);
        msg.addHandler(MSG_GET_GRIDS, this::onGetGrids);
        msg.addHandler(MSG_GET_CELL_DATA, this::onGetCellData);

        pathfinder.setStart(new Point(0, 0));
        pathfinder.setGoal(new Point(0, 0));

        System.out.println("Pathfinder is running");
    }

    private void onSetPos(String type, MessageReader reader) {
        double x = reader.readDouble();
        double y = reader.readDouble();
        Point p = field.getNearestPoint(x, y);
        pathfinder.setStart(p);
    }

    private void onSetGoal(String type, MessageReader reader) {
        double x = reader.readDouble();
        double y = reader.readDouble();
        Point p = field.getNearestPoint(x, y);
        pathfinder.setGoal(p);
    }

    private void onGetFieldInfo(String type, MessageReader reader) {
        msg.prepare(MSG_FIELD_INFO)
                .addDouble(field.getCellSize())
                .addDouble(field.getWidth())
                .addDouble(field.getHeight())
                .addDouble(field.getOriginX())
                .addDouble(field.getOriginY())
                .addInt(field.getCellsX())
                .addInt(field.getCellsY())
                .send();
    }

    private void onGetGrids(String type, MessageReader reader) {
        MessageBuilder builder = msg.prepare(MSG_GRIDS);
        grids.addToMessenger(builder);
        builder.send();
    }

    private void onGetCellData(String type, MessageReader reader) {
        int width = grids.getCellWidth();
        int height = grids.getCellHeight();
        BitfieldGrid out = new BitfieldGrid(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                out.set(x, y, grids.canCellPass(x, y));
            }
        }

        MessageBuilder builder = msg.prepare(MSG_CELL_DATA);
        out.writeToMessengerNoTypeId(builder);
        builder.send();
    }

    public void run() {
        while (true) {
            msg.readMessages();

            // Find path
            List<Point> path = pathfinder.findPath();

            // Send path
            MessageBuilder builder = msg.prepare(MSG_PATH);
            builder.addBoolean(path != null);
            if (path != null) {
                builder.addInt(path.size());
                for (Point p : path) {
                    builder.addDouble(field.convertPointX(p));
                    builder.addDouble(field.convertPointY(p));
                }
            }
            builder.send();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            }
        }
        msg.disconnect();
    }
}
