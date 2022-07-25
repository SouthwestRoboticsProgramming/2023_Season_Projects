package com.swrobotics.pathfinding.lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public final class AStarPathfinder implements Pathfinder {
    private final Grid grid;
    private Node[][] nodes;

    private Point startPoint, goalPoint;

    public AStarPathfinder(Grid grid) {
        this.grid = grid;
    }

    @Override
    public void setStart(Point start) {
        startPoint = start;
    }

    @Override
    public void setGoal(Point goal) {
        goalPoint = goal;
    }

    // Since nodes are stored in a fixed array and never change,
    // identity equals() and hashCode() are fine
    private static class Node implements Comparable<Node> {
        Point position;
        int priority;
        Node cameFrom;
        int costSoFar = Integer.MAX_VALUE;

        @Override
        public int compareTo(Node o) {
            return priority - o.priority;
        }
    }

    private int getNeighbors(Node current, Node[] neighbors) {
        int i = 0;

        int w = grid.getWidth();
        int h = grid.getHeight();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;

                int px = current.position.x + x;
                int py = current.position.y + y;
                if (px >= 0 && px < w && py >= 0 && py < h && grid.canPass(px, py)) {
                    neighbors[i++] = nodes[px][py];
                }
            }
        }

        return i;
    }

    private static final int DIAGONAL_COST = 14;
    private static final int STRAIGHT_COST = 10;
    private int getCost(Node current, Node next) {
        // Will never be checking against self or non-neighbor

        int dx = current.position.x - next.position.x;
        int dy = current.position.y - next.position.y;

        return dx != 0 && dy != 0 ? DIAGONAL_COST : STRAIGHT_COST;
    }

    private int getHeuristic(Node node) {
        int dx = node.position.x - goalPoint.x;
        int dy = node.position.y - goalPoint.y;
        return Math.round(10 * (float) Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public List<Point> findPath() {
        nodes = new Node[grid.getWidth()][grid.getHeight()];
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node node = new Node();
                node.position = new Point(x, y);
                nodes[x][y] = node;
            }
        }

        Node start = nodes[startPoint.x][startPoint.y];
        Node goal = nodes[goalPoint.x][goalPoint.y];

        Set<Node> closed = new HashSet<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        start.priority = 0;
        frontier.add(start);

        Node[] neighbors = new Node[8]; // Maximum neighbor count is 8
        while (!frontier.isEmpty()) {
            Node current = frontier.remove();
            if (current == goal) {
                Node node = current;
                List<Point> out = new ArrayList<>();
                while (node != null) {
                    out.add(0, node.position);
                    node = node.cameFrom;
                }
                return out;
            }

            closed.add(current);

            int count = getNeighbors(current, neighbors);
            for (int i = 0; i < count; i++) {
                Node next = neighbors[i];
                if (closed.contains(next))
                    continue;

                int newCost = current.costSoFar + getCost(current, next);
                if (newCost < next.costSoFar) {
                    next.costSoFar = newCost;
                    next.priority = newCost + getHeuristic(next);
                    frontier.add(next);
                    next.cameFrom = current;
                }
            }
        }

        // No path found
        return null;
    }
}
