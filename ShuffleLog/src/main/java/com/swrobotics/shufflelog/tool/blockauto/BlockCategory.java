package com.swrobotics.shufflelog.tool.blockauto;

import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.opengl.PGraphicsOpenGL;

import java.util.Arrays;
import java.util.List;

import static imgui.ImGui.*;

public final class BlockCategory {
    private static final float BLOCK_PALETTE_SPACING = 5;

    private final String name;
    private final List<Block> blocks;
    private final PApplet app;
    private final PFont font;
    private PGraphicsOpenGL p;

    public BlockCategory(PApplet app, String name, Block... blocks) {
        this.name = name;
        this.app = app;
        this.blocks = Arrays.asList(blocks);
        font = app.getGraphics().textFont;
        p = null;
    }

    private void drawPalette() {
        p.beginDraw();
        {
            ImVec4 bgCol = getStyle().getColor(ImGuiCol.WindowBg);
            p.background(bgCol.x * 255, bgCol.y * 255, bgCol.z * 255);

            p.textFont(font);
            float y = 0;
            for (Block block : blocks) {
                block.render(p, 0, y);
                y += block.getRenderedHeight(font) + BLOCK_PALETTE_SPACING;
            }
        }
        p.endDraw();
    }

    private void redrawPalette() {
        // Initialize graphics with correct size
        float width = 0, height = (blocks.size() - 1) * BLOCK_PALETTE_SPACING;
        for (Block block : blocks) {
            width = Math.max(width, block.getRenderedWidth(font));
            height += block.getRenderedHeight(font);
        }
        if (width == 0 && height == 0)
            return;
        p = (PGraphicsOpenGL) app.createGraphics((int) width, (int) height, PConstants.P2D);

        // Processing has a bug where you need to draw twice for some reason?
        drawPalette();
        drawPalette();
    }

    public void draw() {
        if (!collapsingHeader(name)) return;

        if (p == null)
            redrawPalette();

        if (p != null)
            imageButton(p.getTexture().glName, p.width, p.height, 0, 1, 1, 0, 0);
    }
}
