package com.swrobotics.pathfinding.test;

import imgui.ImVec2;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import static imgui.ImGui.begin;
import static imgui.ImGui.end;
import static imgui.ImGui.getContentRegionAvail;
import static imgui.ImGui.image;
import static processing.core.PConstants.P2D;

public abstract class ProcessingView {
    private final PApplet app;
    private PGraphicsOpenGL g;
    private int pTexture;

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

    public void drawGui() {
        if (begin("Field View")) {
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
                    image(texId, size.x, size.y);

                pTexture = texId;
            }
        }
        end();
    }
}
