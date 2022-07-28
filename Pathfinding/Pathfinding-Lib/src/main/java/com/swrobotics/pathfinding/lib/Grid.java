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

    public boolean lineOfSight(Point s, Point sp) {
        int x0 = s.x;
        int y0 = s.y;
        int x1 = sp.x;
        int y1 = sp.y;
        int dy = y1 - y0;
        int dx = x1 - x0;
        int f = 0;

        int sy;
        int sx;

        if (dy < 0) {
            dy = -dy;
            sy = -1;
        } else {
            sy = 1;
        }

        if (dx < 0) {
            dx = -dx;
            sx = -1;
        } else {
            sx = 1;
        }

        if (dx >= dy) {
            while (x0 != x1) {
                f = f + dy;
                if (f >= dx) {
                    if (!canCellPass(x0 + ((sx - 1)/2), y0 + ((sy - 1)/2))) {
                        return false;
                    }
                    y0 = y0 + sy;
                    f = f - dx;
                }
                if (f != 0 && !canCellPass(x0 + ((sx - 1)/2), y0 + ((sy - 1)/2))) {
                    return false;
                }
                if (dy == 0 && !canCellPass(x0 + ((sx - 1)/2), y0) && !canCellPass(x0 + ((sx - 1)/2), y0 - 1)) {
                    return false;
                }
                x0 = x0 + sx;
            }
        } else {
            while (y0 != y1) {
                f = f + dx;
                if (f >= dy) {
                    if (!canCellPass(x0 + ((sx - 1)/2), y0 + ((sy - 1)/2))) {
                        return false;
                    }
                    x0 = x0 + sx;
                    f = f - dy;
                }
                if (f != 0 && !canCellPass(x0 + ((sx - 1)/2), y0 + ((sy - 1)/2))) {
                    return false;
                }
                if (dx == 0 && !canCellPass(x0, y0 + ((sy - 1)/2)) && !canCellPass(x0 - 1, y0 + ((sy - 1)/2))) {
                    return false;
                }
                y0 = y0 + sy;
            }
        }

        return true;
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
