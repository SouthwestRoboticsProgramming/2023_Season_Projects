package com.swrobotics.shufflelog.tool;

import imgui.type.ImBoolean;

import static imgui.ImGui.*;

public final class MenuBarTool implements Tool {
    private final ImBoolean showDemo;

    public MenuBarTool() {
        showDemo = new ImBoolean(false);
    }

    @Override
    public void process() {
        if (beginMainMenuBar()) {
            if (beginMenu("Debug")) {
                menuItem("Show demo", null, showDemo);

                endMenu();
            }

            endMainMenuBar();
        }

        if (showDemo.get())
            showDemoWindow();
    }
}
