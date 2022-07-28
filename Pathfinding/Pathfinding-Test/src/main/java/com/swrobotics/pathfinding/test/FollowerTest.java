package com.swrobotics.pathfinding.test;

import com.swrobotics.pathfinding.lib.Point;
import processing.core.PGraphics;

import static processing.core.PConstants.*;

public final class FollowerTest {
    private static final float VISUAL_DIAMETER = 1f;

    private double driveSpeed;
    private long lastTime;
    private double posX, posY;

    public FollowerTest() {
        reset(0, 0);
    }

    public void reset(double x, double y) {
        posX = x;
        posY = y;
        lastTime = -1;
    }

    public void update(PGraphics g, Point nextPathPoint) {
        long time = System.nanoTime();
        double delta = 0;
        if (lastTime != -1)
            delta = (time - lastTime) / 1_000_000_000.0;
        lastTime = time;

        if (Math.round(posX) == nextPathPoint.x && Math.round(posY) == nextPathPoint.y)
            return;

        double dx = nextPathPoint.x - posX;
        double dy = nextPathPoint.y - posY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        posX += dx / distance * driveSpeed * delta;
        posY += dy / distance * driveSpeed * delta;

        // draw
        g.noStroke();
        g.fill(255);
        g.ellipseMode(CENTER);
        g.ellipse((float) posX, (float) posY, VISUAL_DIAMETER, VISUAL_DIAMETER);

        g.fill(255, 0, 0);
        g.ellipse(nextPathPoint.x, nextPathPoint.y, 0.5f, 0.5f);
    }

    public double getDriveSpeed() {
        return driveSpeed;
    }

    public void setDriveSpeed(double driveSpeed) {
        this.driveSpeed = driveSpeed;
    }

    public double getX() {
        return posX;
    }

    public double getY() {
        return posY;
    }
}
