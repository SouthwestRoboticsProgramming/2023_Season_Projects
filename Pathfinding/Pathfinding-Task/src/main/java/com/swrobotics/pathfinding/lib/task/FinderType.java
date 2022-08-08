package com.swrobotics.pathfinding.lib.task;

import com.swrobotics.pathfinding.lib.finder.AStarPathfinder;
import com.swrobotics.pathfinding.lib.finder.Pathfinder;
import com.swrobotics.pathfinding.lib.finder.ThetaStarPathfinder;
import com.swrobotics.pathfinding.lib.grid.Grid;

public enum FinderType {
    A_STAR {
        @Override
        public Pathfinder create(Grid grid) {
            return new AStarPathfinder(grid);
        }
    },
    THETA_STAR {
        @Override
        public Pathfinder create(Grid grid) {
            return new ThetaStarPathfinder(grid);
        }
    };

    public abstract Pathfinder create(Grid grid);
}
