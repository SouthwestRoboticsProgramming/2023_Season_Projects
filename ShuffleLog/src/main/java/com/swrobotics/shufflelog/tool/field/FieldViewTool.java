package com.swrobotics.shufflelog.tool.field;

import com.swrobotics.shufflelog.tool.ViewportTool;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import processing.core.PApplet;
import processing.core.PGraphics;

import static imgui.ImGui.*;

public final class FieldViewTool extends ViewportTool {
    public FieldViewTool(PApplet app) {
        super(app, "Field View");
    }

    @Override
    protected void drawViewportContent(PGraphics g) {
        g.background(128);
    }

    @Override
    protected void drawGuiContent() {
        if (beginTable("view_layout", 2, ImGuiTableFlags.BordersInner | ImGuiTableFlags.Resizable)) {
            tableSetupColumn("viewport", ImGuiTableColumnFlags.WidthStretch);
            tableSetupColumn("layers", ImGuiTableColumnFlags.WidthFixed);

            tableNextColumn();
            drawViewport();
            tableNextColumn();
            text("Layers go hereeee");

            endTable();
        }
    }
}
