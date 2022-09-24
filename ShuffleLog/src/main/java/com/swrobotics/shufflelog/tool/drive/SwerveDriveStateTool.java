package com.swrobotics.shufflelog.tool.drive;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.tool.ViewportTool;
import com.swrobotics.shufflelog.util.Cooldown;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImFloat;
import processing.core.PGraphics;

import static imgui.ImGui.*;

public final class SwerveDriveStateTool extends ViewportTool {
    private static final String MSG_GET_MODULE_DEFS = "Swerve:GetModuleDefs";

    private static final String MSG_MODULE_DEFS = "Swerve:ModuleDefs";
    private static final String MSG_MODULE_STATES = "Swerve:ModuleStates";

    private final MessengerClient msg;
    private final Cooldown getDefCooldown;
    private final float[] driveScale;

    private SwerveDriveState state;

    public SwerveDriveStateTool(ShuffleLog log) {
        super(log, "Swerve State");

        msg = log.getMsg();
        msg.addHandler(MSG_MODULE_DEFS, this::onModuleDefs);
        msg.addHandler(MSG_MODULE_STATES, this::onModuleStates);
        getDefCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);
        driveScale = new float[] {0.05f};

        state = null;
    }

    private void onModuleDefs(String type, MessageReader reader) {
        state = new SwerveDriveState(reader);
    }

    public void onModuleStates(String type, MessageReader reader) {
        if (state == null) return;

        state.readState(reader);
    }

    private static final float PADDING_PX = 75;

    @Override
    protected void drawViewportContent(PGraphics g) {
        g.background(0);
        if (state == null)
            return;

        float xRange = state.getMaxX() - state.getMinX();
        float yRange = state.getMaxY() - state.getMinY();
        float xScale = (g.width - PADDING_PX * 2) / xRange;
        float yScale = (g.height - PADDING_PX * 2) / yRange;
        float scale = Math.min(xScale, yScale);
        float strokeMul = 1 / scale;

        g.translate(g.width / 2f, g.height / 2f);
        g.scale(scale, -scale); // Invert Y axis to match field coordinates

        state.draw(g, strokeMul, driveScale[0], true, true, true);
    }

    @Override
    protected void drawGuiContent() {
        if (beginTable("layout", 2, ImGuiTableFlags.BordersInner | ImGuiTableFlags.Resizable)) {
            if (state == null && getDefCooldown.request()) {
                msg.send(MSG_GET_MODULE_DEFS);
            }

            tableNextColumn();
            drawViewport();
            tableNextColumn();

            if (state != null) {
                text("Vector scale");
                sliderFloat("##scale", driveScale, 0, 1);
                state.showGui();
            } else {
                text("Waiting to receive module layout...");
            }

            endTable();
        }
    }
}
