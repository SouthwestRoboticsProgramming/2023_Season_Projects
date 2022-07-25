package com.swrobotics.pathfinding.test;

import com.swrobotics.pathfinding.lib.BitfieldGrid;
import processing.core.PApplet;
import processing.core.PGraphics;

import static imgui.ImGui.*;
import static processing.core.PConstants.*;

public final class FieldView extends ProcessingView {
    private static final int MODE_DRAW = 0;
    private static final int MODE_PATH = 1;

    private static final float SMOOTH = 12f;

    private final int[] fieldSize;
    private BitfieldGrid grid;
    private boolean viewportHovered;
    private SmoothFloat posX, posY, scale;
    private boolean hasInitialized;

    private int mode;
    private boolean paintState;

    public FieldView(PApplet app) {
        super(app, "Field View");

        fieldSize = new int[2];
        fieldSize[0] = 10;
        fieldSize[1] = 10;
        grid = new BitfieldGrid(fieldSize[0], fieldSize[1]);

        viewportHovered = false;
        posX = new SmoothFloat(SMOOTH, -5);
        posY = new SmoothFloat(SMOOTH, -5);
        scale = new SmoothFloat(SMOOTH, 1);
        hasInitialized = false;

        mode = MODE_DRAW;
    }

    private int getHoveredCellX() {
        return (int) Math.floor((mouseX - g.width / 2f) / scale.get() - posX.get());
    }

    private int getHoveredCellY() {
        return (int) Math.floor((mouseY - g.height / 2f) / scale.get() - posY.get());
    }

    private boolean isGridPosValid(int x, int y) {
        return x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight();
    }

    @Override
    public void mousePressed() {
        int x = getHoveredCellX();
        int y = getHoveredCellY();
        boolean valid = viewportHovered && isGridPosValid(x, y);

        if (mode == MODE_DRAW && mouseButton == LEFT) {
            boolean currentState = valid && grid.canPass(x, y);
            paintState = !currentState;
            if (valid)
                grid.set(x, y, paintState);
        }
    }

    @Override
    public void mouseDragged() {
        int x = getHoveredCellX();
        int y = getHoveredCellY();

        if (mode == MODE_DRAW) {
            if (mouseButton == LEFT) {
                if (isGridPosValid(x, y) && viewportHovered)
                    grid.set(x, y, paintState);
            } else if (mouseButton == RIGHT || mouseButton == CENTER) {
                posX.set(posX.getTarget() + (mouseX - pmouseX) / scale.get());
                posY.set(posY.getTarget() + (mouseY - pmouseY) / scale.get());
            }
        }
    }

    @Override
    public void mouseScrolled(float x, float y) {
        if (!viewportHovered)
            return;

        float canvasX = (mouseX - g.width / 2f) / scale.get() - posX.get();
        float canvasY = (mouseY - g.height / 2f) / scale.get() - posY.get();
        scale.set(scale.getTarget() * (1 + y * -0.1f));
        posX.set((mouseX - g.width / 2f) / scale.getTarget() - canvasX);
        posY.set((mouseY - g.height / 2f) / scale.getTarget() - canvasY);
    }

    private void strokeWidth(PGraphics g, float widthPixels) {
        g.strokeWeight(widthPixels / scale.get());
    }

    @Override
    protected void drawViewportContent(PGraphics g) {
        posX.step();
        posY.step();
        scale.step();

        g.background(32);
        g.translate(g.width / 2f, g.height / 2f);
        g.scale(scale.get());
        g.translate(posX.get(), posY.get());

        // Cells
        int hoverX = getHoveredCellX();
        int hoverY = getHoveredCellY();
        g.noStroke();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                if (!grid.canPass(x, y))
                    g.fill(201, 101, 18);
                else if (x == hoverX && y == hoverY)
                    g.fill(255, 255, 255, 32);
                else
                    g.noFill();

                g.rect(x, y, 1, 1);
            }
        }

        // Edges
        strokeWidth(g, 1.5f);
        g.stroke(128, 128, 128, 255);
        for (int x = 0; x <= grid.getWidth(); x++) {
            g.line(x, 0, x, grid.getHeight());
        }
        for (int y = 0; y <= grid.getHeight(); y++) {
            g.line(0, y, grid.getWidth(), y);
        }
    }

    private void resetZoom(boolean now) {
        float requiredScaleX = g.width / (float) (grid.getWidth() + 2);
        float requiredScaleY = g.height / (float) (grid.getHeight() + 2);
        float min = Math.min(requiredScaleX, requiredScaleY);

        if (now)
            scale.setNow(min);
        else
            scale.set(min);
    }

    @Override
    protected void drawGuiContent() {
        alignTextToFramePadding();
        text("Field size:");
        sameLine();
        setNextItemWidth(-1);
        if (sliderInt2("##size_input", fieldSize, 1, 100)) {
            BitfieldGrid temp = grid;
            grid = new BitfieldGrid(fieldSize[0], fieldSize[1]);
            grid.copyFrom(temp);
        }

        text("View:");
        sameLine();
        boolean resetPos = false, resetZoom = false;
        if (button("Reset")) {
            resetPos = true;
            resetZoom = true;
        }
        sameLine();
        if (button("Reset Position"))
            resetPos = true;
        sameLine();
        if (button("Reset Zoom"))
            resetZoom = true;
        if (resetPos) {
            posX.set(-grid.getWidth() / 2.0f);
            posY.set(-grid.getHeight() / 2.0f);
        }
        if (resetZoom)
            resetZoom(false);

        if (!hasInitialized && g != null) {
            posX.set(-grid.getWidth() / 2.0f);
            posY.set(-grid.getHeight() / 2.0f);
            resetZoom(true);

            hasInitialized = true;
        }

        text("Mode:");
        sameLine();
        if (radioButton("Draw", mode == MODE_DRAW))
            mode = MODE_DRAW;
        sameLine();
        if (radioButton("Path", mode == MODE_PATH))
            mode = MODE_PATH;

        if (button("Clear grid"))
            grid.clear();

        drawViewport();
        viewportHovered = isItemHovered();
    }
}
