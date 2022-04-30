package com.swrobotics.shufflelog;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;

public final class ShuffleLog extends Application {
    private boolean hasInitialized = false;

    @Override
    protected void configure(Configuration config) {
        config.setTitle("ShuffleLog");
    }

    private void initialize() {
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename("layout.ini");
    }

    @Override
    public void process() {
        if (!hasInitialized) {
            initialize();
            hasInitialized = true;
        }

        ImGui.showDemoWindow();
    }

    public static void main(String[] args) {
        launch(new ShuffleLog());
    }
}
