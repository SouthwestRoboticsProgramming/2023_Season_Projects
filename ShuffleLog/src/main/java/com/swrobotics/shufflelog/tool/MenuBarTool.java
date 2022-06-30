package com.swrobotics.shufflelog.tool;

import imgui.extension.implot.ImPlot;
import imgui.type.ImBoolean;

import static imgui.ImGui.*;

public final class MenuBarTool implements Tool {
    private final ImBoolean showDemo, showPlotDemo;
    private final ImBoolean demoOpen, plotDemoOpen;

    public MenuBarTool() {
        showDemo = new ImBoolean(false);
        showPlotDemo = new ImBoolean(false);

        demoOpen = new ImBoolean(true);
        plotDemoOpen = new ImBoolean(true);
    }

    @Override
    public void process() {
        if (beginMainMenuBar()) {
            if (beginMenu("Debug")) {
                menuItem("Show demo", null, showDemo);
                menuItem("Show plot demo", null, showPlotDemo);

                endMenu();
            }

            endMainMenuBar();
        }

        if (showDemo.get())
            showDemoWindow(demoOpen);
        if (showPlotDemo.get())
            ImPlot.showDemoWindow(plotDemoOpen);
    }
}
