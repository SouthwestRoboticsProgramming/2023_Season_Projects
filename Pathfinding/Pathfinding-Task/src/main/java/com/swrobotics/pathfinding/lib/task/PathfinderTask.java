package com.swrobotics.pathfinding.lib.task;

import com.swrobotics.messenger.client.MessageBuilder;
import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.pathfinding.lib.Field;
import com.swrobotics.pathfinding.lib.Point;
import com.swrobotics.pathfinding.lib.finder.Pathfinder;
import com.swrobotics.pathfinding.lib.grid.GridUnion;

import java.io.File;
import java.util.List;

public final class PathfinderTask {
    private static final File CONFIG_FILE = new File("config.json");

    // Main API
    private static final String MSG_SET_POS  = "Pathfinder:SetPos";
    private static final String MSG_SET_GOAL = "Pathfinder:SetGoal";
    private static final String MSG_PATH = "Pathfinder:Path";

    // ShuffleLog API
    private static final String MSG_GET_FIELD_INFO = "Pathfinder:GetFieldInfo";
    private static final String MSG_FIELD_INFO = "Pathfinder:FieldInfo";

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
        pathfinder = config.getFinderType().create(grids);

        msg.addHandler(MSG_SET_POS, this::onSetPos);
        msg.addHandler(MSG_SET_GOAL, this::onSetGoal);

        msg.addHandler(MSG_GET_FIELD_INFO, this::onGetFieldInfo);

        pathfinder.setStart(new Point(0, 0));
        pathfinder.setGoal(new Point(0, 0));

        System.out.println("Pathfinder is ready");
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
