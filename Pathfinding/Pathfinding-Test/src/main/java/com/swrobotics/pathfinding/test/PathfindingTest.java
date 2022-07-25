package com.swrobotics.pathfinding.test;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import processing.core.PApplet;
import processing.event.MouseEvent;

public final class PathfindingTest extends PApplet {
    private ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private GLFWScrollCallback prevScrollCallback;

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

        // Custom scroll handler because the Processing handler is bad
        prevScrollCallback = GLFW.glfwSetScrollCallback(windowHandle, this::scrollCallback);

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init();

        field = new FieldView(this);

        // Hack to fix bug in ImGui GLFW implementation where cursor
        // position is not registered until it leaves and re-enters
        // the window
        // Note: This will not work if viewports are enabled
        imGuiGlfw.cursorEnterCallback(windowHandle, true);
    }

    private void scrollCallback(long win, double x, double y) {
        if (prevScrollCallback != null)
            prevScrollCallback.invoke(win, x, y);

        updateMouseInfo();
        field.mouseScrolled((float) x, (float) y);
    }

    @Override
    public void draw() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        background(13, 26, 38);

        ImGui.dockSpaceOverViewport();
        ImGui.showDemoWindow();
        field.drawGui();

        flush();
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private void updateMouseInfo() {
        field.setMouseInfo(mouseX, mouseY, pmouseX, pmouseY, mouseButton);
    }

    @Override
    public void mousePressed() {
        updateMouseInfo();
        field.mousePressed();
    }

    @Override
    public void mouseReleased() {
        updateMouseInfo();
        field.mouseReleased();
    }

    @Override
    public void mouseMoved() {
        updateMouseInfo();
        field.mouseMoved();
    }

    @Override
    public void mouseDragged() {
        updateMouseInfo();
        field.mouseDragged();
    }

    public static void main(String[] args) {
        PApplet.main(PathfindingTest.class);
    }
}
