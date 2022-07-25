package com.swrobotics.pathfinding.lib;

import java.util.List;

public interface Pathfinder {
    void setStart(Point start);
    void setGoal(Point goal);

    List<Point> findPath();

    void run();
}
