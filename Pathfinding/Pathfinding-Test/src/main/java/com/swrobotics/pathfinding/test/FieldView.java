package com.swrobotics.pathfinding.test;

import com.swrobotics.pathfinding.lib.finder.AStarPathfinder;
import com.swrobotics.pathfinding.lib.grid.BitfieldGrid;
import com.swrobotics.pathfinding.lib.finder.Pathfinder;
import com.swrobotics.pathfinding.lib.Point;
import com.swrobotics.pathfinding.lib.finder.ThetaStarPathfinder;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;

import static imgui.ImGui.*;
import static processing.core.PConstants.*;

public final class FieldView extends ProcessingView {
    private static final int MODE_DRAW = 0;
    private static final int MODE_FILL = 1;
    private static final int MODE_PATH = 2;

    private static final int FINDER_A_STAR = 0;
    private static final int FINDER_THETA_STAR = 1;

    private static final float SMOOTH = 12f;

    private final int[] fieldSize;
    private BitfieldGrid grid;
    private Pathfinder pathfinder;

    private boolean viewportHovered;
    private SmoothFloat posX, posY, scale;
    private boolean hasInitialized;

    private Point start, goal;

    private int mode;
    private int finderType;
    private boolean paintState;
    private int downX, downY;
    private boolean down;

    private float[] randomFillPercent;

    private FollowerTest follower = new FollowerTest();

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

        start = new Point(1, 1);
        goal = new Point(8, 8);

        mode = MODE_DRAW;
        finderType = FINDER_THETA_STAR;

        randomFillPercent = new float[] {50};

        createPathfinder();
    }

    private int getHoveredCellX() {
        return (int) Math.floor((mouseX - g.width / 2f) / scale.get() - posX.get());
    }

    private int getHoveredCellY() {
        return (int) Math.floor((mouseY - g.height / 2f) / scale.get() - posY.get());
    }

    private boolean isCellPosValid(int x, int y) {
        return x >= 0 && x < grid.getCellWidth() && y >= 0 && y < grid.getCellHeight();
    }

    private int getHoveredPointX() {
        return (int) Math.floor((mouseX - g.width / 2f) / scale.get() - posX.get() + 0.5);
    }

    private int getHoveredPointY() {
        return (int) Math.floor((mouseY - g.height / 2f) / scale.get() - posY.get() + 0.5);
    }

    private boolean isPointPosValid(int x, int y) {
        return x >= 0 && x < grid.getPointWidth() && y >= 0 && y < grid.getPointHeight();
    }

    @Override
    public void mousePressed() {
        if (!viewportHovered)
            return;

        if (mode == MODE_DRAW && mouseButton == LEFT) {
            int x = getHoveredCellX();
            int y = getHoveredCellY();
            boolean valid = isCellPosValid(x, y);

            boolean currentState = valid && grid.canCellPass(x, y);
            paintState = !currentState;
            if (valid)
                grid.set(x, y, paintState);
        } else if (mode == MODE_FILL) {
            int x = getHoveredCellX();
            int y = getHoveredCellY();
            boolean valid = isCellPosValid(x, y);
            boolean currentState = valid && grid.canCellPass(x, y);
            paintState = !currentState;
            if (valid) {
                downX = x;
                downY = y;
                down = true;
            }
        } else if (mode == MODE_PATH) {
            int x = getHoveredPointX();
            int y = getHoveredPointY();
            boolean valid = isPointPosValid(x, y);

            if (valid) {
                if (mouseButton == LEFT) {
                    follower.reset(x, y);
                } else if (mouseButton == RIGHT) {
                    goal = new Point(x, y);
                }
            }
        }
    }

    @Override
    public void mouseReleased() {
        down = false;

        if (!viewportHovered)
            return;

        if (mode == MODE_FILL) {
            down = false;

            int x = getHoveredCellX();
            int y = getHoveredCellY();
            boolean valid = isCellPosValid(x, y);
            if (!valid) return;

            int minX = Math.min(x, downX);
            int maxX = Math.max(x, downX);
            int minY = Math.min(y, downY);
            int maxY = Math.max(y, downY);

            for (int r = minY; r <= maxY; r++)
                for (int c = minX; c <= maxX; c++)
                    grid.set(c, r, paintState);
        }
    }

    private void dragView() {
        posX.set(posX.getTarget() + (mouseX - pmouseX) / scale.get());
        posY.set(posY.getTarget() + (mouseY - pmouseY) / scale.get());
    }

    @Override
    public void mouseDragged() {
        if (!viewportHovered)
            return;

        if (mode == MODE_DRAW) {
            int x = getHoveredCellX();
            int y = getHoveredCellY();
            boolean valid = isCellPosValid(x, y);

            if (mouseButton == LEFT) {
                if (valid)
                    grid.set(x, y, paintState);
            } else if (mouseButton == RIGHT || mouseButton == CENTER) {
                dragView();
            }
        } else if (mode == MODE_PATH) {
            int x = getHoveredPointX();
            int y = getHoveredPointY();
            boolean valid = isPointPosValid(x, y);

            if (mouseButton == LEFT && valid) {
                follower.reset(x, y);
            } else if (mouseButton == RIGHT && valid) {
                goal = new Point(x, y);
            } else if (mouseButton == CENTER) {
                dragView();
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

        start = new Point((int) Math.round(follower.getX()), (int) Math.round(follower.getY()));

        g.background(32);
        g.translate(g.width / 2f, g.height / 2f);
        g.scale(scale.get());
        g.translate(posX.get(), posY.get());

        // Cells
        int hoverX = getHoveredCellX();
        int hoverY = getHoveredCellY();
        g.noStroke();
        for (int x = 0; x < grid.getCellWidth(); x++) {
            for (int y = 0; y < grid.getCellHeight(); y++) {
                if (!grid.canCellPass(x, y))
                    g.fill(201, 101, 18);
                else if (x == hoverX && y == hoverY)
                    g.fill(255, 255, 255, 32);
                else
                    g.noFill();

                g.rect(x, y, 1, 1);
            }
        }

        if (down) {
            int x = getHoveredCellX();
            int y = getHoveredCellY();
            boolean valid = isCellPosValid(x, y);
            if (valid) {
                int minX = Math.min(x, downX);
                int maxX = Math.max(x, downX);
                int minY = Math.min(y, downY);
                int maxY = Math.max(y, downY);

                g.fill(255, 255, 255, 32);
                g.noStroke();
                for (int r = minY; r <= maxY; r++)
                    for (int c = minX; c <= maxX; c++)
                        g.rect(c, r, 1, 1);
            }
        }

        // Edges
        strokeWidth(g, 1.5f);
        g.stroke(128, 128, 128, 255);
        for (int x = 0; x < grid.getPointWidth(); x++) {
            g.line(x, 0, x, grid.getCellHeight());
        }
        for (int y = 0; y < grid.getPointHeight(); y++) {
            g.line(0, y, grid.getCellWidth(), y);
        }

        // Points
        strokeWidth(g, 1);
        g.ellipseMode(CENTER);
        g.stroke(27, 196, 101, 128);
        g.fill(27, 196, 101);
        float startSize = start.equals(goal) ? 0.6f : 0.5f;
        g.ellipse(start.x, start.y, startSize, startSize);
        g.stroke(44, 62, 199, 128);
        g.fill(44, 62, 199);
        g.ellipse(goal.x, goal.y, 0.5f, 0.5f);

        // Path
        pathfinder.setStart(start);
        pathfinder.setGoal(goal);
        List<Point> path = pathfinder.findPath();
        if (path != null) {
            if (path.size() >= 2)
                follower.update(g, path.get(1));

            strokeWidth(g, 4);
            g.stroke(214, 196, 32, 128);
            g.beginShape(LINE_STRIP);
            for (Point p : path)
                g.vertex(p.x, p.y);
            g.endShape();
            strokeWidth(g, 2);
            g.stroke(214, 196, 32);
            g.beginShape(LINE_STRIP);
            for (Point p : path)
                g.vertex(p.x, p.y);
            g.endShape();
        }
    }

    private void resetZoom(boolean now) {
        float requiredScaleX = g.width / (float) (grid.getCellWidth() + 2);
        float requiredScaleY = g.height / (float) (grid.getCellHeight() + 2);
        float min = Math.min(requiredScaleX, requiredScaleY);

        if (now)
            scale.setNow(min);
        else
            scale.set(min);
    }

    private void clampStartAndEnd() {
        start = new Point(Math.min(start.x, grid.getPointWidth() - 1), Math.min(start.y, grid.getPointHeight() - 1));
        goal = new Point(Math.min(goal.x, grid.getPointWidth() - 1), Math.min(goal.y, grid.getPointHeight() - 1));
    }

    private void createPathfinder() {
        switch (finderType) {
            case FINDER_A_STAR:
                pathfinder = new AStarPathfinder(grid);
                break;
            case FINDER_THETA_STAR:
                pathfinder = new ThetaStarPathfinder(grid);
                break;
        }
    }

    @Override
    protected void drawGuiContent() {
        alignTextToFramePadding();
        text("Field size:");
        sameLine();
        setNextItemWidth(-1);
        if (sliderInt2("##size_input", fieldSize, 1, 250)) {
            BitfieldGrid temp = grid;
            grid = new BitfieldGrid(fieldSize[0], fieldSize[1]);
            grid.copyFrom(temp);
            createPathfinder();
            clampStartAndEnd();
        }

        alignTextToFramePadding();
        text("Follower drive speed:");
        sameLine();
        float[] driveSpeed = {(float) follower.getDriveSpeed()};
        if (sliderFloat("##drive_speed", driveSpeed, 0, 25)) {
            follower.setDriveSpeed(driveSpeed[0]);
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
            posX.set(-grid.getCellWidth() / 2.0f);
            posY.set(-grid.getCellHeight() / 2.0f);
        }
        if (resetZoom)
            resetZoom(false);

        if (!hasInitialized && g != null) {
            posX.set(-grid.getCellWidth() / 2.0f);
            posY.set(-grid.getCellHeight() / 2.0f);
            resetZoom(true);

            hasInitialized = true;
        }

        text("Mode:");
        sameLine();
        if (radioButton("Draw", mode == MODE_DRAW))
            mode = MODE_DRAW;
        sameLine();
        if (radioButton("Fill", mode == MODE_FILL))
            mode = MODE_FILL;
        sameLine();
        if (radioButton("Path", mode == MODE_PATH))
            mode = MODE_PATH;

        if (button("Clear grid"))
            grid.clear();
        sameLine();
        if (button("Randomly fill grid")) {
            for (int x = 0; x < grid.getCellWidth(); x++) {
                for (int y = 0; y < grid.getCellHeight(); y++) {
                    grid.set(x, y, Math.random() > randomFillPercent[0]/100f);
                }
            }
        }
        sameLine();
        text("Random percent:");
        sameLine();
        setNextItemWidth(-1);
        sliderFloat("##random_percent", randomFillPercent, 0, 100);

        text("Pathfinder algorithm: ");
        sameLine();
        boolean finderChanged = false;
        if (radioButton("A*", finderType == FINDER_A_STAR)) {
            finderType = FINDER_A_STAR;
            finderChanged = true;
        }
        sameLine();
        if (radioButton("Theta*", finderType == FINDER_THETA_STAR)) {
            finderType = FINDER_THETA_STAR;
            finderChanged = true;
        }
        if (finderChanged) {
            createPathfinder();
        }

        drawViewport();
        viewportHovered = isItemHovered();
    }
}
