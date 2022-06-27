package com.swrobotics.shufflelog;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.tool.MenuBarTool;
import com.swrobotics.shufflelog.tool.MessengerTool;
import com.swrobotics.shufflelog.tool.NetworkTablesTool;
import com.swrobotics.shufflelog.tool.Tool;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;

import java.util.ArrayList;
import java.util.List;

public final class ShuffleLog extends Application {
    private final List<Tool> tools = new ArrayList<>();
    private MessengerClient msg;

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

        tools.add(new MenuBarTool());
        tools.add(new MessengerTool(this));
        tools.add(new NetworkTablesTool());
    }

    @Override
    public void process() {
        msg.readMessages();

        for (Tool tool : tools) {
            tool.process();
        }
    }

    @Override
    protected void disposeImGui() {
        super.disposeImGui();

        msg.disconnect();
    }

    public MessengerClient getMsg() {
        return msg;
    }

    public void setMsg(MessengerClient msg) {
        this.msg = msg;
    }

    public static void main(String[] args) {
        launch(new ShuffleLog());
    }
}
