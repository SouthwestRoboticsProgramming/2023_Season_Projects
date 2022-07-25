package com.swrobotics.pathfinding.lib;

public abstract class Grid {
    protected final int width;
    protected final int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract boolean canPass(int x, int y);
    public boolean canPass(Point p) {
        return canPass(p.x, p.y);
    }

    public boolean canLinePass(Point p1, Point p2) {
        // DDA algorithm

        float dirX = p2.x - p1.x;
        float dirY = p2.y - p1.y;
        float dirLen = (float) Math.sqrt(dirX*dirX + dirY*dirY);
        dirX /= dirLen;
        dirY /= dirLen;

        double posX = p1.x + 0.5;
        double posY = p1.y + 0.5;

        int mapX = p1.x;
        int mapY = p1.y;

        double sideDistX;
        double sideDistY;

        double deltaDistX = (dirX == 0) ? 1e30 : Math.abs(1 / dirX);
        double deltaDistY = (dirY == 0) ? 1e30 : Math.abs(1 / dirY);

        int stepX;
        int stepY;

        boolean hit = false;

        if (dirX < 0) {
            stepX = -1;
            sideDistX = (posX - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - posX) * deltaDistX;
        }
        if (dirY < 0) {
            stepY = -1;
            sideDistY = (posY - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - posY) * deltaDistY;
        }

        while (!hit) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
            }

            if (!canPass(mapX, mapY))
                hit = true;

            if (mapX == p2.x && mapY == p2.y)
                break;
        }

        return !hit;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
