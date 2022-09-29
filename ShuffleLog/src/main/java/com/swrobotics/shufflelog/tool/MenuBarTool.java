package com.swrobotics.shufflelog.tool;

import com.swrobotics.shufflelog.debug.ImNodesTest;
import imgui.extension.implot.ImPlot;
import imgui.type.ImBoolean;

import static imgui.ImGui.*;

public final class MenuBarTool implements Tool {
    private final ImBoolean showDemo, showPlotDemo, showDebugWindow, showNodeTest;
    private final ImBoolean demoOpen, plotDemoOpen;

    public MenuBarTool() {
        showDemo = new ImBoolean(false);
        showPlotDemo = new ImBoolean(false);
        showDebugWindow = new ImBoolean(false);
        showNodeTest = new ImBoolean(false);

        demoOpen = new ImBoolean(true);
        plotDemoOpen = new ImBoolean(true);
    }

    private void showDebugWindow() {
        if (begin("Debug window")) {
            text("Debugger");
        }
        end();
    }

    @Override
    public void process() {
        if (beginMainMenuBar()) {
            if (beginMenu("Debug")) {
                menuItem("Show demo", null, showDemo);
                menuItem("Show plot demo", null, showPlotDemo);
                menuItem("Show debug window", null, showDebugWindow);
                menuItem("Show node test", null, showNodeTest);

                endMenu();
            }

            endMainMenuBar();
        }

        if (showDemo.get())
            showDemoWindow(demoOpen);
        if (showPlotDemo.get())
            ImPlot.showDemoWindow(plotDemoOpen);
        if (showDebugWindow.get())
            showDebugWindow();
        if (showNodeTest.get())
            ImNodesTest.show();
    }
}
