package com.swrobotics.shufflelog;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.debug.SwerveDebugTool;
import com.swrobotics.shufflelog.profile.Profiler;
import com.swrobotics.shufflelog.tool.blockauto.BlockAutoTool;
import com.swrobotics.shufflelog.tool.data.DataLogTool;
import com.swrobotics.shufflelog.tool.MenuBarTool;
import com.swrobotics.shufflelog.tool.field.FieldViewTool;
import com.swrobotics.shufflelog.tool.messenger.MessengerTool;
import com.swrobotics.shufflelog.tool.data.NetworkTablesTool;
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
import processing.core.PFont;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ShuffleLog extends PApplet {
    private static final String LAYOUT_FILE = "layout.ini";
    private static final String DEFAULT_LAYOUT_FILE = "default-layout.ini";

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private final List<Tool> tools = new ArrayList<>();
    private final List<Tool> addedTools = new ArrayList<>();
    private final List<Tool> removedTools = new ArrayList<>();
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

        saveDefaultLayout();

        ImGui.createContext();
        ImPlot.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(LAYOUT_FILE);
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        Styles.applyDark();

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init();

        // Set default font
        try {
            textFont(new PFont(getClass().getClassLoader().getResourceAsStream("fonts/PTSans-Regular-14.vlw")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tools.add(new MenuBarTool());
        tools.add(new MessengerTool(this));
        DataLogTool dataLogTool = new DataLogTool(this);
        tools.add(dataLogTool);
        tools.add(new NetworkTablesTool(threadPool, dataLogTool));
        tools.add(new ShuffleLogProfilerTool());
        tools.add(new RobotProfilerTool(msg));
        tools.add(new SchedulerTool(msg));
        tools.add(new TaskManagerTool(this, "TaskManager"));
        tools.add(new FieldViewTool(this));
        tools.add(new BlockAutoTool(this));

        // Temporarily turn ShuffleLog into a swerve drive debugger
//        tools.add(new SwerveDebugTool(this));

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
        tools.addAll(addedTools);
        tools.removeAll(removedTools);
        addedTools.clear();
        removedTools.clear();

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

    @Override
    public void keyPressed() {
        for (Tool tool : tools) {
            tool.onKeyPress(key, keyCode);
        }
    }

    @Override
    public void keyReleased() {
        for (Tool tool : tools) {
            tool.onKeyRelease(key, keyCode);
        }
    }

    public void addTool(Tool tool) {
        addedTools.add(tool);
    }

    public void removeTool(Tool tool) {
        removedTools.add(tool);
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

    private void saveDefaultLayout() {
        File file = new File(LAYOUT_FILE);
        if (!file.exists()) {
            try {
                InputStream stream = ShuffleLog.class.getClassLoader().getResourceAsStream(DEFAULT_LAYOUT_FILE);
                StreamUtil.copy(Objects.requireNonNull(stream), new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(ShuffleLog.class);
    }
}
