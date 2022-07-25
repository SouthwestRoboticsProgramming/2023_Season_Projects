package com.swrobotics.pathfinding.lib;

public abstract class Grid {
    private static final double EPSILON = 0.00001;

    protected final int width;
    protected final int height;

    // Sizes are in number of cells, points is one larger
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract boolean canCellPass(int x, int y);
    public boolean canCellPass(Point p) {
        return canCellPass(p.x, p.y);
    }

    public boolean canEdgePass(Point p1, Point p2) {
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;
        int ox = (dx - 2) / 2;
        int oy = (dy - 2) / 2;

        if (dx != 0 && dy != 0) {
            return canCellPass(p1.x + ox, p1.y + oy);
        } else {
            if (dx > 0)
                return canCellPass(p1) && canCellPass(p1.x, p1.y - 1);
            if (dx < 0)
                return canCellPass(p1.x - 1, p1.y) && canCellPass(p1.x - 1, p1.y - 1);
            if (dy > 0)
                return canCellPass(p1) && canCellPass(p1.x - 1, p1.y);
            if (dy < 0)
                return canCellPass(p1.x, p1.y - 1) && canCellPass(p1.x - 1, p1.y - 1);

            throw new IllegalStateException();
        }
    }

    public boolean canLinePass(Point p1, Point p2) {
        // DDA algorithm

        float dirX = p2.x - p1.x;
        float dirY = p2.y - p1.y;
        float dirLen = (float) Math.sqrt(dirX*dirX + dirY*dirY);
        dirX /= dirLen;
        dirY /= dirLen;

        double posX = p1.x + EPSILON;
        double posY = p1.y + EPSILON;

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

            if (!canCellPass(mapX, mapY))
                hit = true;

            if (mapX == p2.x && mapY == p2.y)
                break;
        }

        return !hit;
    }

    public int getCellWidth() {
        return width;
    }

    public int getCellHeight() {
        return height;
    }

    public int getPointWidth() {
        return width + 1;
    }

    public int getPointHeight() {
        return height + 1;
    }
}
