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

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);

        ImGuiIO io = ImGui.getIO();
        io.setIniFilename("layout.ini");

        Styles.applyDark();
    }

    @Override
    public void process() {
        ImGui.showDemoWindow();
    }

    public static void main(String[] args) {
        launch(new ShuffleLog());
    }
}
