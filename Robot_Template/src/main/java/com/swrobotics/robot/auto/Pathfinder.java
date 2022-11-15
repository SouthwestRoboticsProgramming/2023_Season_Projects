package com.swrobotics.robot.auto;

import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.messenger.MessageReader;
import com.swrobotics.lib.messenger.MessengerClient;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.robot.subsystem.Localization;

import java.util.ArrayList;
import java.util.List;

public final class Pathfinder implements Subsystem {
    private static final String MSG_SET_POS = "Pathfinder:SetPos";
    private static final String MSG_SET_GOAL = "Pathfinder:SetGoal";
    private static final String MSG_PATH = "Pathfinder:Path";

    private final MessengerClient msg;
    private final Localization loc;
    private final List<Vec2d> path;
    private boolean pathValid;

    public Pathfinder(MessengerClient msg, Localization loc) {
        this.msg = msg;
        this.loc = loc;
        path = new ArrayList<>();

        msg.addHandler(MSG_PATH, this::onPath);
    }

    @Override
    public void periodic() {
        setPosition(loc.getPosition());
    }

    private void setPosition(Vec2d pos) {
        msg.prepare(MSG_SET_POS)
                .addDouble(pos.x)
                .addDouble(pos.y)
                .send();
    }

    public void setTarget(Vec2d pos) {
        msg.prepare(MSG_SET_GOAL)
                .addDouble(pos.x)
                .addDouble(pos.y)
                .send();
    }

    private void onPath(String type, MessageReader reader) {
        pathValid = reader.readBoolean();
        if (pathValid) {
            int pointCount = reader.readInt();
            path.clear();
            for (int i = 0; i < pointCount; i++) {
                double x = reader.readDouble();
                double y = reader.readDouble();
                path.add(new Vec2d(x, y));
            }
        }
    }

    public boolean isPathValid() {
        return pathValid;
    }

    public List<Vec2d> getPath() {
        return new ArrayList<>(path);
    }
}
