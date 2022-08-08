package com.swrobotics.pathfinding.lib.finder;

import com.swrobotics.pathfinding.lib.Point;

import java.util.List;

public interface Pathfinder {
    void setStart(Point start);
    void setGoal(Point goal);

    List<Point> findPath();
}
