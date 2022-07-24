package com.swrobotics.shufflelog.tool;

import com.swrobotics.shufflelog.ShuffleLog;
import edu.wpi.first.networktables.NetworkTableEntry;
import imgui.ImVec2;
import imgui.extension.implot.flag.ImPlotLocation;
import imgui.extension.implot.flag.ImPlotOrientation;
import imgui.flag.ImGuiCond;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;
import static imgui.extension.implot.ImPlot.*;

public final class DataLogTool implements Tool {
    private static final int HISTORY_RETENTION_TIME = 10; // Seconds
    private static final double Y_PADDING = 0.05; // Percentage of Y span

    private static final class DataPoint {
        double x, y;

        public DataPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class DataStorage {
        List<DataPoint> history = new ArrayList<>();
        double minY, maxY;

        void recalculateYRange() {
            minY = Double.POSITIVE_INFINITY;
            maxY = Double.NEGATIVE_INFINITY;

            for (DataPoint point : history) {
                if (point.y < minY)
                    minY = point.y;
                if (point.y > maxY)
                    maxY = point.y;
            }
        }

        void addDataPoint(double x, double y) {
            history.add(new DataPoint(x, y));
            while (x - history.get(0).x > HISTORY_RETENTION_TIME)
                history.remove(0);

            recalculateYRange();
        }

        double[][] getDataArrays() {
            double[] x = new double[history.size()];
            double[] y = new double[history.size()];
            for (int i = 0; i < history.size(); i++) {
                DataPoint point = history.get(i);
                x[i] = point.x;
                y[i] = point.y;
            }
            return new double[][] {x, y};
        }
    }

    private static final class Graph {
        String path;
        String name;
        NetworkTableEntry entry;

        List<DataStorage> storage = new ArrayList<>();
        {
            storage.add(new DataStorage());
            storage.add(new DataStorage());
            storage.add(new DataStorage());
        }
    }

    private final ShuffleLog log;
    private final List<Graph> graphs;

    public DataLogTool(ShuffleLog log) {
        this.log = log;
        graphs = new ArrayList<>();
    }

    public void addGraph(String path, String name, NetworkTableEntry entry) {
        Graph graph = new Graph();
        graph.path = path;
        graph.name = name;
        graph.entry = entry;
        graphs.add(graph);
    }

    private void sampleData(Graph graph, double time) {
        NetworkTableEntry entry = graph.entry;

        int requiredStorageCount = -1;
        switch (entry.getType()) {
            case kBoolean:
            case kDouble: requiredStorageCount = 1; break;
            case kBooleanArray: requiredStorageCount = entry.getBooleanArray(new boolean[0]).length; break;
            case kDoubleArray: requiredStorageCount = entry.getDoubleArray(new double[0]).length; break;
            default:
                throw new IllegalStateException("Can't graph " + entry.getType().name());
        }

        if (graph.storage.size() < requiredStorageCount) {
            for (int i = graph.storage.size(); i < requiredStorageCount; i++) {
                graph.storage.add(new DataStorage());
            }
        }
        if (graph.storage.size() > requiredStorageCount) {
            int count = graph.storage.size() - requiredStorageCount;
            for (int i = 0; i < count; i++)
                graph.storage.remove(graph.storage.size() - 1);
        }

        switch (entry.getType()) {
            case kBoolean: graph.storage.get(0).addDataPoint(time, entry.getBoolean(false) ? 1 : 0); break;
            case kDouble: graph.storage.get(0).addDataPoint(time, entry.getDouble(0)); break;
            case kBooleanArray: {
                boolean[] b = entry.getBooleanArray(new boolean[0]);
                for (int i = 0; i < b.length; i++) {
                    graph.storage.get(i).addDataPoint(time, b[i] ? 1 : 0);
                }
                break;
            }
            case kDoubleArray: {
                double[] d = entry.getDoubleArray(new double[0]);
                for (int i = 0; i < d.length; i++) {
                    graph.storage.get(i).addDataPoint(time, d[i]);
                }
            }
        }
    }

    private static final ImVec2 GRAPH_SIZE = new ImVec2(-1, 200);
    private void showGraph(Graph graph, double time) {
        sampleData(graph, time);

        if (!collapsingHeader(graph.path)) {
            return;
        }

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (DataStorage storage : graph.storage) {
            minX = Math.min(minX, storage.history.get(0).x);
            maxX = Math.max(maxX, storage.history.get(storage.history.size() - 1).x);
            minY = Math.min(minY, storage.minY);
            maxY = Math.max(maxY, storage.maxY);
        }

        double pad = (maxY - minY) * Y_PADDING;

        // When the graph is completely flat, add a bit of padding so the line is visible
        if (pad < 0.00001) pad = 0.1;

        setNextPlotLimits(minX, maxX, minY - pad, maxY + pad, ImGuiCond.Always);
        if (beginPlot(graph.path, "Time (s)", "Value", GRAPH_SIZE, 0, 0, 0)) {
            setLegendLocation(ImPlotLocation.East, ImPlotOrientation.Vertical, true);
            int size = graph.storage.size();
            int i = 0;
            for (DataStorage storage : graph.storage) {
                double[][] data = storage.getDataArrays();
                plotLine(size > 1 ? String.valueOf(i) : "", data[0], data[1], data[0].length, 0);
                i++;
            }
            endPlot();
        }
    }

    @Override
    public void process() {
        double time = log.getTimestamp();

        if (begin("Data Log")) {
            for (Graph graph : graphs) {
                showGraph(graph, time);
            }
        }
        end();
    }
}
