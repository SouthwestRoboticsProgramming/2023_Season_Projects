package com.swrobotics.shufflelog.tool.field;

import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.ViewportTool;
import com.swrobotics.shufflelog.tool.field.path.PathfindingLayer;
import com.swrobotics.shufflelog.util.SmoothFloat;
import imgui.ImGuiIO;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class FieldViewTool extends ViewportTool {
    public static final double WIDTH = 8.2296;
    public static final double HEIGHT = 16.4592;
    private static final float PADDING_PX = 10;

    private final List<FieldLayer> layers;
    private final SmoothFloat posX, posY;
    private final SmoothFloat scale;
    private boolean shouldRescale;
    private boolean viewportRotated;
    private boolean firstFrame;

    public FieldViewTool(ShuffleLog log) {
        super(log, "Field View", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        layers = new ArrayList<>();

        layers.add(new PathfindingLayer(log.getMsg()));

        posX = new SmoothFloat(12, 0);
        posY = new SmoothFloat(12, 0);
        scale = new SmoothFloat(12, 1);

        shouldRescale = true;
        firstFrame = true;
    }

    private float calcReqScale(float px, float field) {
        return (px - PADDING_PX * 2) / field;
    }

    @Override
    protected void drawViewportContent(PGraphics g) {
        g.background(32);
        g.translate(g.width / 2f, g.height / 2f);

        float fieldX = (float) WIDTH;
        float fieldY = (float) HEIGHT;
        float posX = this.posX.get();
        float posY = this.posY.get();
        // If the field could be bigger horizontally, rotate it
        if (g.width > g.height) {
            g.rotate((float) Math.PI / 2);
            fieldX = (float) HEIGHT;
            fieldY = (float) WIDTH;
            viewportRotated = true;
        } else {
            viewportRotated = false;
        }

        if (!firstFrame && shouldRescale) {
            float scaleX = calcReqScale(g.width, fieldX);
            float scaleY = calcReqScale(g.height, fieldY);
            scale.set(Math.min(scaleX, scaleY));
            shouldRescale = false;
        }
        float s = scale.get();
        g.scale(s, -s); // flip y
        g.translate(posX, posY);

        for (FieldLayer layer : layers) {
            g.pushMatrix();
            layer.draw(g, s);
            g.popMatrix();
        }

        firstFrame = false;
    }

    @Override
    protected void drawGuiContent() {
        boolean resetPos = false, resetZoom = false;
        if (button("Reset view")) {
            resetPos = true;
            resetZoom = true;
        }
        sameLine();
        if (button("Reset position"))
            resetPos = true;
        sameLine();
        if (button("Reset zoom"))
            resetZoom = true;

        if (resetPos) {
            posX.set(0);
            posY.set(0);
        }
        if (resetZoom)
            shouldRescale = true;

        posX.step();
        posY.step();
        scale.step();

        if (beginTable("view_layout", 2, ImGuiTableFlags.BordersInner | ImGuiTableFlags.Resizable)) {
            tableSetupColumn("viewport", ImGuiTableColumnFlags.WidthStretch);
            tableSetupColumn("layers", ImGuiTableColumnFlags.WidthFixed);

            tableNextColumn();
            drawViewport();
            if (isItemHovered()) {
                ImGuiIO io = getIO();
                float deltaX = io.getMouseDeltaX();
                float deltaY = io.getMouseDeltaY();
                float wheel = io.getMouseWheel();

                if (isMouseDown(ImGuiMouseButton.Left)) {
                    float s = scale.get();
                    if (viewportRotated) {
                        posX.set(posX.getTarget() + deltaY / s);
                        posY.set(posY.getTarget() + deltaX / s);
                    } else {
                        posX.set(posX.getTarget() + deltaX / s);
                        posY.set(posY.getTarget() - deltaY / s);
                    }
                }

                // Scroll to zoom
                scale.set(scale.getTarget() * (1 + wheel * -0.1f));
            }
            tableNextColumn();
            for (FieldLayer layer : layers) {
                if (collapsingHeader(layer.getName())) {
                    layer.showGui();
                }
            }

            endTable();
        }
    }
}
