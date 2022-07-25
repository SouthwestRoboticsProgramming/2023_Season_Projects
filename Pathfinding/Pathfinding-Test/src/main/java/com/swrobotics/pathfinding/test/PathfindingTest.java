package com.swrobotics.pathfinding.test;

import com.swrobotics.pathfinding.lib.BitfieldGrid;
import com.swrobotics.pathfinding.lib.Point;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import processing.core.PApplet;
import processing.event.MouseEvent;

// TODO: Merge this entire module with ShuffleLog
public final class PathfindingTest extends PApplet {
    private ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private FieldView field;

    @Override
    public void settings() {
        size(1280, 720, P2D);
    }

    @Override
    public void setup() {
        surface.setResizable(true);

        long windowHandle = (long) surface.getNative();

        ImGui.createContext();
        ImGui.getIO().setConfigFlags(ImGuiConfigFlags.DockingEnable);
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init();

        field = new FieldView(this);

        // Hack to fix bug in ImGui GLFW implementation where cursor
        // position is not registered until it leaves and re-enters
        // the window
        // Note: This will not work if viewports are enabled
        imGuiGlfw.cursorEnterCallback(windowHandle, true);
    }

    @Override
    public void draw() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        background(0);

        ImGui.showDemoWindow();
        field.drawGui();

        flush();
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    @Override
    public void mouseDragged(MouseEvent event) {}

    public static void main(String[] args) {
        PApplet.main(PathfindingTest.class);
    }
}
