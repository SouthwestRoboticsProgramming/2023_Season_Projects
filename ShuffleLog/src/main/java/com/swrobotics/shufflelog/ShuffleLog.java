package com.swrobotics.shufflelog;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.profile.Profiler;
import com.swrobotics.shufflelog.tool.data.DataLogTool;
import com.swrobotics.shufflelog.tool.MenuBarTool;
import com.swrobotics.shufflelog.tool.messenger.MessengerTool;
import com.swrobotics.shufflelog.tool.NetworkTablesTool;
import com.swrobotics.shufflelog.tool.profile.RobotProfilerTool;
import com.swrobotics.shufflelog.tool.profile.ShuffleLogProfilerTool;
import com.swrobotics.shufflelog.tool.Tool;
import com.swrobotics.shufflelog.tool.scheduler.SchedulerTool;
import com.swrobotics.shufflelog.tool.taskmanager.TaskManagerTool;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: Use proper ImGui backend
public final class ShuffleLog extends PApplet {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private final List<Tool> tools = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private MessengerClient msg;
    private long startTime;

    @Override
    public void settings() {
        size(1280, 720, P2D);
    }

    @Override
    public void setup() {
        surface.setResizable(true);
        long windowHandle = (long) surface.getNative();

        ImGui.createContext();
        ImPlot.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename("layout.ini");
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        Styles.applyDark();

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init();

        tools.add(new MenuBarTool());
        tools.add(new MessengerTool(this));
        DataLogTool dataLogTool = new DataLogTool(this);
        tools.add(dataLogTool);
        tools.add(new NetworkTablesTool(threadPool, dataLogTool));
        tools.add(new ShuffleLogProfilerTool());
        tools.add(new RobotProfilerTool(msg));
        tools.add(new SchedulerTool(msg));
        tools.add(new TaskManagerTool(msg, "TaskManager"));

        startTime = System.currentTimeMillis();
    }

    public double getTimestamp() {
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }

    @Override
    public void draw() {
        Profiler.beginMeasurements("Root");

        Profiler.push("Begin GUI frame");
        imGuiGlfw.flushEvents();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        Profiler.pop();

        Profiler.push("Read Messages");
        msg.readMessages();
        Profiler.pop();

        background(210);

        ImGui.dockSpaceOverViewport();

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
        imGuiGl3.renderDrawData(ImGui.getDrawData());
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
