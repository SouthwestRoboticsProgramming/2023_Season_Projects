package com.swrobotics.pathfinding.test;

import imgui.ImVec2;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import static imgui.ImGui.*;
import static processing.core.PConstants.P2D;

public abstract class ProcessingView {
    private final PApplet app;
    private PGraphicsOpenGL g;
    private int pTexture;

    private float originX, originY;
    protected float mouseX, mouseY;
    protected int mouseButton;

    public ProcessingView(PApplet app) {
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

    protected abstract void drawContent(PGraphics g);

    public final void setMouseInfo(float x, float y, int button) { mouseX = x - originX; mouseY = y - originY; mouseButton = button; }
    protected void mousePressed() {}
    protected void mouseReleased() {}
    protected void mouseMoved() {}
    protected void mouseDragged() {}

    public final void drawGui() {
        if (begin("Field View")) {
            ImVec2 window = getWindowPos();
            originX = window.x;
            originY = window.y;

            ImVec2 cursor = getCursorPos();
            originX += cursor.x;
            originY += cursor.y;

            ImVec2 size = getContentRegionAvail();
            if (size.x > 0 && size.y > 0) {
                boolean shouldShowThisFrame = prepareGraphics((int) size.x, (int) size.y);

                g.beginDraw();
                drawContent(g);
                g.endDraw();

                // PGraphicsOpenGL seems to require one frame to get started, so
                // use the previous frame if we just created a new graphics object
                int texId;
                if (shouldShowThisFrame)
                    texId = g.getTexture().glName;
                else
                    texId = pTexture;

                if (texId != -1)
                    imageButton(texId, size.x, size.y, 0, 1, 1, 0, 0);

                pTexture = texId;
            }
        }
        end();
    }
}
