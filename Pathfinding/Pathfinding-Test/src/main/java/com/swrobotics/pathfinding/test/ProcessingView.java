package com.swrobotics.pathfinding.test;

import imgui.ImVec2;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import static imgui.ImGui.*;
import static processing.core.PConstants.P2D;

public abstract class ProcessingView {
    private final PApplet app;
    private final String title;
    private int pTexture;

    protected PGraphicsOpenGL g;
    private float originX, originY;
    protected float mouseX, mouseY;
    protected float pmouseX, pmouseY;
    protected int mouseButton;

    public ProcessingView(PApplet app, String title) {
        this.title = title;
        this.app = app;
        pTexture = -1;
    }

    private boolean prepareGraphics(int w, int h) {
        if (g == null || g.width != w || g.height != h) {
            g = (PGraphicsOpenGL) app.createGraphics(w, h, P2D);
            return false;
        }
        return true;
    }

    // Important: Mouse data is only valid within this method and mouse events
    protected abstract void drawViewportContent(PGraphics g);

    public final void setMouseInfo(float x, float y, float px, float py, int button) { mouseX = x - originX; mouseY = y - originY; pmouseX = px - originX; pmouseY = py - originY; mouseButton = button; }
    protected void mousePressed() {}
    protected void mouseReleased() {}
    protected void mouseMoved() {}
    protected void mouseDragged() {}
    protected void mouseScrolled(float x, float y) {}

    protected final void drawViewport() {
        ImVec2 size = getContentRegionAvail();
        drawViewport(size.x, size.y);
    }

    protected final void drawViewport(float w, float h) {
        updateMouseOrigin();
        if (w > 0 && h > 0) {
            boolean shouldShowThisFrame = prepareGraphics((int) w, (int) h);

            g.beginDraw();
            drawViewportContent(g);
            g.endDraw();

            // PGraphicsOpenGL seems to require one frame to get started, so
            // use the previous frame if we just created a new graphics object
            int texId;
            if (shouldShowThisFrame)
                texId = g.getTexture().glName;
            else
                texId = pTexture;

            if (texId != -1)
                imageButton(texId, w, h, 0, 1, 1, 0, 0);

            pTexture = texId;
        }
    }

    protected final void updateMouseOrigin() {
        ImVec2 window = getWindowPos();
        originX = window.x;
        originY = window.y;

        ImVec2 cursor = getCursorPos();
        originX += cursor.x;
        originY += cursor.y;
    }

    protected void drawGuiContent() {
        drawViewport();
    }

    public final void drawGui() {
        if (begin(title)) {
            drawGuiContent();
        }
        end();
    }
}
