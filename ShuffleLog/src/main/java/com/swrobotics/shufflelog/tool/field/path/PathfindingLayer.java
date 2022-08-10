package com.swrobotics.shufflelog.tool.field.path;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.tool.field.FieldLayer;
import com.swrobotics.shufflelog.util.Cooldown;
import imgui.type.ImBoolean;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class PathfindingLayer implements FieldLayer {
    private static final String MSG_SET_POS  = "Pathfinder:SetPos";
    private static final String MSG_SET_GOAL = "Pathfinder:SetGoal";
    private static final String MSG_PATH = "Pathfinder:Path";

    private static final String MSG_GET_FIELD_INFO = "Pathfinder:GetFieldInfo";
    private static final String MSG_FIELD_INFO = "Pathfinder:FieldInfo";

    private final MessengerClient msg;
    private final Cooldown reqFieldInfoCooldown;

    private final ImBoolean showGridLines;
    private final ImBoolean showGridCells;
    private final ImBoolean showShapes;
    private final ImBoolean showPath;

    private FieldInfo fieldInfo;
    private List<Point> path;

    public PathfindingLayer(MessengerClient msg) {
        this.msg = msg;
        reqFieldInfoCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);

        msg.addHandler(MSG_PATH, this::onPath);
        msg.addHandler(MSG_FIELD_INFO, this::onFieldInfo);

        showGridLines = new ImBoolean(true);
        showGridCells = new ImBoolean(true);
        showShapes = new ImBoolean(true);
        showPath = new ImBoolean(true);

        fieldInfo = null;
        path = null;
    }

    private void onPath(String type, MessageReader reader) {
        boolean valid = reader.readBoolean();
        if (valid) {
            if (path != null)
                path.clear();
            else
                path = new ArrayList<>();

            int count = reader.readInt();
            for (int i = 0; i < count; i++) {
                double x = reader.readDouble();
                double y = reader.readDouble();
                path.add(new Point(x, y));
            }
        } else {
            path = null;
        }
    }

    private void onFieldInfo(String type, MessageReader reader) {
        fieldInfo = new FieldInfo(reader);
    }

    @Override
    public String getName() {
        return "Pathfinding";
    }

    @Override
    public void draw(PGraphics g, float metersScale) {
        if (fieldInfo == null) {
            if (reqFieldInfoCooldown.request())
                msg.send(MSG_GET_FIELD_INFO);

            g.fill(255, 0, 0);
            g.noStroke();
            g.rect(0, 0, 1, 1);
            return;
        }

        msg.prepare(MSG_SET_POS)
                .addDouble(0)
                .addDouble(0)
                .send();

        msg.prepare(MSG_SET_GOAL)
                .addDouble(1)
                .addDouble(1)
                .send();

        boolean lines = showGridLines.get();
        boolean cells = showGridCells.get();
        boolean shapes = showShapes.get();
        boolean path = showPath.get();

        float strokeMul = 1 / metersScale;
        g.pushMatrix();
        {
            // Transform into cell space
            float cellSize = (float) fieldInfo.getCellSize();
            g.scale(cellSize, -cellSize);
            g.translate((float) -fieldInfo.getOriginX(), (float) -fieldInfo.getOriginY());
            float cellStrokeMul = strokeMul / cellSize;

            int cellsX = fieldInfo.getCellsX();
            int cellsY = fieldInfo.getCellsY();

            // Show grid lines
            if (lines) {
                g.strokeWeight(1.5f * cellStrokeMul);
                g.stroke(128);

                for (int x = 0; x <= cellsX; x++) {
                    g.line(x, 0, x, cellsY);
                }
                for (int y = 0; y <= cellsY; y++) {
                    g.line(0, y, cellsX, y);
                }
            }
        }
        g.popMatrix();

        // Show path
        if (path && this.path != null) {
            g.strokeWeight(4 * strokeMul);
            g.stroke(214, 196, 32, 128);
            g.beginShape(PConstants.LINE_STRIP);
            for (Point p : this.path)
                g.vertex((float) p.x, (float) p.y);
            g.endShape();

            g.strokeWeight(2 * strokeMul);
            g.stroke(214, 196, 32);
            g.beginShape(PConstants.LINE_STRIP);
            for (Point p : this.path)
                g.vertex((float) p.x, (float) p.y);
            g.endShape();
        }
    }

    @Override
    public void showGui() {
        checkbox("Show grid lines", showGridLines);
        checkbox("Show grid cells", showGridCells);
        checkbox("Show shapes", showShapes);
        checkbox("Show path", showPath);
    }
}
