package com.swrobotics.shufflelog;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.profile.Profiler;
import com.swrobotics.shufflelog.tool.DataLogTool;
import com.swrobotics.shufflelog.tool.MenuBarTool;
import com.swrobotics.shufflelog.tool.MessengerTool;
import com.swrobotics.shufflelog.tool.NetworkTablesTool;
import com.swrobotics.shufflelog.tool.ShuffleLogProfilerTool;
import com.swrobotics.shufflelog.tool.Tool;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiMouseButton;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ShuffleLog extends PApplet {
    private final List<Tool> tools = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private ProcessingImGuiBackend gui;
    private MessengerClient msg;
    private long startTime;

    @Override
    public void settings() {
        size(1280, 720, P2D);
    }

    @Override
    public void setup() {
        surface.setResizable(true);

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename("layout.ini");
        Styles.applyDark();

        gui = new ProcessingImGuiBackend();
        gui.init();

        ImPlot.createContext();

        tools.add(new MenuBarTool());
        tools.add(new MessengerTool(this));
        DataLogTool dataLogTool = new DataLogTool(this);
        tools.add(dataLogTool);
        tools.add(new NetworkTablesTool(threadPool, dataLogTool));
        tools.add(new ShuffleLogProfilerTool());

        startTime = System.currentTimeMillis();
    }

    public double getTimestamp() {
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }

    @Override
    public void draw() {
        Profiler.beginMeasurements("Root");

        Profiler.push("Begin GUI frame");
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(width, height);
        io.setDeltaTime(1 / 60.0f);
        io.setMousePos(mouseX, mouseY);
        io.setMouseDown(ImGuiMouseButton.Left, mousePressed);
        ImGui.newFrame();
        Profiler.pop();

        Profiler.push("Read Messages");
        msg.readMessages();
        Profiler.pop();

        background(210);

        for (Tool tool : tools) {
            Profiler.push(tool.getClass().getSimpleName());
            tool.process();
            Profiler.pop();
        }

        Profiler.push("Render GUI");
        Profiler.push("Flush");
        flush();
        Profiler.pop();
        Profiler.push("Render draw data");
        ImGui.render();
        gui.renderDrawData(ImGui.getDrawData());
        Profiler.pop();
        Profiler.pop();

        Profiler.endMeasurements();
    }

    public MessengerClient getMsg() {
        return msg;
    }

    public void setMsg(MessengerClient msg) {
        this.msg = msg;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public static void main(String[] args) {
        PApplet.main(ShuffleLog.class);
    }
}
