package com.swrobotics.shufflelog.tool.drive;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.tool.ViewportTool;
import com.swrobotics.shufflelog.util.Cooldown;
import processing.core.PGraphics;

import static imgui.ImGui.*;

public final class SwerveDriveStateTool extends ViewportTool {
    private static final String MSG_GET_MODULE_DEFS = "Swerve:GetModuleDefs";

    private static final String MSG_MODULE_DEFS = "Swerve:ModuleDefs";
    private static final String MSG_MODULE_STATES = "Swerve:ModuleStates";

    private final MessengerClient msg;
    private final Cooldown getDefCooldown;

    private SwerveDriveState state;

    public SwerveDriveStateTool(ShuffleLog log) {
        super(log, "Swerve State");

        msg = log.getMsg();
        getDefCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);

        state = null;
    }

    @Override
    protected void drawViewportContent(PGraphics g) {
        g.background(0);
    }

    @Override
    protected void drawGuiContent() {
        if (beginTable("layout", 2)) {
            if (state == null && getDefCooldown.request()) {
                msg.send(MSG_GET_MODULE_DEFS);
            }

            tableNextColumn();
            drawViewport();
            tableNextColumn();

            // UI

            endTable();
        }
    }
}
