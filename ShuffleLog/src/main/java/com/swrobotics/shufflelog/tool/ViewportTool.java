package com.swrobotics.shufflelog.tool;

import imgui.ImVec2;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import static imgui.ImGui.begin;
import static imgui.ImGui.end;
import static imgui.ImGui.getContentRegionAvail;
import static imgui.ImGui.imageButton;
import static processing.core.PConstants.P2D;

public abstract class ViewportTool implements Tool {
    private final PApplet app;
    private final String title;
    private int pTexture;
    private PGraphicsOpenGL g;

    public ViewportTool(PApplet app, String title) {
        this.app = app;
        this.title = title;
        pTexture = -1;
    }

    protected abstract void drawViewportContent(PGraphics g);

    private boolean prepareGraphics(int w, int h) {
        if (g == null || g.width != w || g.height != h) {
            g = (PGraphicsOpenGL) app.createGraphics(w, h, P2D);
            return false;
        }
        return true;
    }

    protected final void drawViewport() {
        ImVec2 size = getContentRegionAvail();
        drawViewport(size.x, size.y);
    }

    protected final void drawViewport(float w, float h) {
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

    protected void drawGuiContent() {
        drawViewport();
    }

    @Override
    public void process() {
        if (begin(title)) {
            drawGuiContent();
        }
        end();
    }
}