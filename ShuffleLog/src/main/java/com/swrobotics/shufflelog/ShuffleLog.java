package com.swrobotics.shufflelog;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.profile.Profiler;
import com.swrobotics.shufflelog.tool.MenuBarTool;
import com.swrobotics.shufflelog.tool.MessengerTool;
import com.swrobotics.shufflelog.tool.NetworkTablesTool;
import com.swrobotics.shufflelog.tool.ShuffleLogProfilerTool;
import com.swrobotics.shufflelog.tool.Tool;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.ImPlotContext;

import java.util.ArrayList;
import java.util.List;

public final class ShuffleLog extends Application {
    private final List<Tool> tools = new ArrayList<>();
    private ImPlotContext plotCtx;
    private MessengerClient msg;

    @Override
    protected void configure(Configuration config) {
        config.setTitle("ShuffleLog");
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        plotCtx = ImPlot.createContext();

        ImGuiIO io = ImGui.getIO();
        io.setIniFilename("layout.ini");

        Styles.applyDark();

        tools.add(new MenuBarTool());
        tools.add(new MessengerTool(this));
        tools.add(new NetworkTablesTool());
        tools.add(new ShuffleLogProfilerTool());
    }

    @Override
    public void process() {
        Profiler.beginMeasurements("Root");

        Profiler.push("Read Messages");
        msg.readMessages();
        Profiler.pop();

        for (Tool tool : tools) {
            Profiler.push(tool.getClass().getSimpleName());
            tool.process();
            Profiler.pop();
        }

        Profiler.endMeasurements();
    }

    @Override
    protected void disposeImGui() {
        ImPlot.destroyContext(plotCtx);
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
