package com.swrobotics.pathfinding.lib;

public final class Field {
    private final double cellSize; // meters

    private final int cellsX, cellsY; // cells
    private final double originX, originY; // cells

    /**
     * Constructs a new Field.
     *
     * @param cellSize size of each cell in meters
     * @param width width of the field in meters
     * @param height height of the field in meters
     * @param originX origin X as percentage of width (lower is left)
     * @param originY origin Y as percentage of height (lower is down)
     */
    public Field(double cellSize, double width, double height, double originX, double originY) {
        this.cellSize = cellSize;
        cellsX = (int) Math.ceil(width / cellSize);
        cellsY = (int) Math.ceil(height / cellSize);
        this.originX = originX * cellsX;
        this.originY = originY * cellsY;
    }

    /**
     * Gets the X position of the center of a cell in meters relative
     * to the field origin.
     *
     * @param cellX cell X position
     * @return cell center X in meters
     */
    public double getCellCenterX(int cellX) {
        return (cellX - originX + 0.5) * cellSize;
    }

    /**
     * Gets the Y position of the center of a cell in meters relative
     * to the field origin.
     *
     * @param cellY cell Y position
     * @return cell center Y in meters
     */
    public double getCellCenterY(int cellY) {
        return (cellY - originY + 0.5) * cellSize;
    }

    public int getCellsX() {
        return cellsX;
    }

    public int getCellsY() {
        return cellsY;
    }
}
