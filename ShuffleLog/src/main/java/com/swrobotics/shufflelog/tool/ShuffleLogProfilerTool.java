package com.swrobotics.shufflelog.tool;

import com.swrobotics.shufflelog.profile.ProfileNode;
import com.swrobotics.shufflelog.profile.Profiler;
import imgui.flag.ImGuiTableFlags;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static imgui.ImGui.*;

public final class ShuffleLogProfilerTool extends ProfilerTool {
    private final String vendor, renderer, version, glslVersion;

    public ShuffleLogProfilerTool() {
        super("ShuffleLog Profiler");

        // Get OpenGL info for debugging
        vendor = GL11.glGetString(GL11.GL_VENDOR);
        renderer = GL11.glGetString(GL11.GL_RENDERER);
        version = GL11.glGetString(GL11.GL_VERSION);
        glslVersion = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
    }

    @Override
    protected void showHeader() {
        text("OpenGL info:");
        if (beginTable("header", 2, ImGuiTableFlags.SizingStretchProp)) {
            tableNextColumn();
            text("Vendor:");
            tableNextColumn();
            text(vendor);

            tableNextColumn();
            text("Renderer:");
            tableNextColumn();
            text(renderer);

            tableNextColumn();
            text("Version:");
            tableNextColumn();
            text(version);

            tableNextColumn();
            text("GLSL Version:");
            tableNextColumn();
            text(glslVersion);

            endTable();
        }

        separator();
    }

    @Override
    protected ProfileNode getLastData() {
        return Profiler.getLastData();
    }
}
