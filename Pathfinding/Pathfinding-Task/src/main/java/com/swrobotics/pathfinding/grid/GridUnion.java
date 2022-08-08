package com.swrobotics.pathfinding.grid;

import java.util.HashSet;
import java.util.Set;

public final class GridUnion extends Grid {
    private final Set<Grid> children;

    public GridUnion(int width, int height) {
        super(width, height);
        children = new HashSet<>();
    }

    // Cell is passable if all children's cells are passable
    @Override
    public boolean canCellPass(int x, int y) {
        for (Grid grid : children) {
            if (!grid.canCellPass(x, y)) {
                return false;
            }
        }
        return true;
    }

    public void addGrid(Grid grid) {
        if (grid.getCellWidth() != getCellWidth() || grid.getCellHeight() != getCellHeight())
            throw new IllegalArgumentException("Grid size is not compatible");

        children.add(grid);
    }

    public void removeGrid(Grid grid) {
        children.remove(grid);
    }
}
