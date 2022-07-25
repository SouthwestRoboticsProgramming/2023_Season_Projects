package com.swrobotics.shufflelog;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.profile.Profiler;
import com.swrobotics.shufflelog.tool.data.DataLogTool;
import com.swrobotics.shufflelog.tool.MenuBarTool;
import com.swrobotics.shufflelog.tool.messenger.MessengerTool;
import com.swrobotics.shufflelog.tool.NetworkTablesTool;
import com.swrobotics.shufflelog.tool.ShuffleLogProfilerTool;
import com.swrobotics.shufflelog.tool.Tool;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: Use proper ImGui backend
public final class ShuffleLog extends PApplet {
    private final List<Tool> tools = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private ProcessingImGuiBackend gui;
    private MessengerClient msg;
    private long startTime;

    private boolean leftMouse, middleMouse, rightMouse;

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

        int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = imKey(TAB);
        keyMap[ImGuiKey.LeftArrow] = imKeyCode(LEFT);
        keyMap[ImGuiKey.RightArrow] = imKeyCode(RIGHT);
        keyMap[ImGuiKey.UpArrow] = imKeyCode(UP);
        keyMap[ImGuiKey.DownArrow] = imKeyCode(DOWN);
        keyMap[ImGuiKey.Delete] = imKey(DELETE);
        keyMap[ImGuiKey.Backspace] = imKey(BACKSPACE);
        keyMap[ImGuiKey.Space] = imKey(' ');
        keyMap[ImGuiKey.Enter] = imKey(ENTER);
        keyMap[ImGuiKey.Escape] = imKey(ESC);
        keyMap[ImGuiKey.A] = imKey('a');
        keyMap[ImGuiKey.C] = imKey('c');
        keyMap[ImGuiKey.V] = imKey('v');
        keyMap[ImGuiKey.X] = imKey('x');
        keyMap[ImGuiKey.Y] = imKey('y');
        keyMap[ImGuiKey.Z] = imKey('z');
        io.setKeyMap(keyMap);

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
    public void mousePressed() {
        switch (mouseButton) {
            case LEFT: leftMouse = true; break;
            case RIGHT: rightMouse = true; break;
            case CENTER: middleMouse = true; break;
        }
    }

    @Override
    public void mouseReleased() {
        switch (mouseButton) {
            case LEFT: leftMouse = false; break;
            case RIGHT: rightMouse = false; break;
            case CENTER: middleMouse = false; break;
        }
    }

    @Override
    public void keyTyped() {
        ImGui.getIO().addInputCharacter(key);
    }

    private int imKeyCode(int code) {
        if (code > 0x7F)
            System.out.println("Warning: truncated keycode");
        return (code & 0x7F) | 0x80;
    }

    private int imKey(int key) {
        if (key == RETURN)
            key = ENTER;

        if (key > 0x7F)
            System.out.println("Warning: truncated key");
        return key & 0x7F;
    }

    private int convertKey() {
        return key == CODED ? imKeyCode(keyCode) : imKey(key);
    }

    private void keyEvent(boolean down) {
        ImGuiIO io = ImGui.getIO();
        io.setKeysDown(convertKey(), down);

        io.setKeyCtrl(io.getKeysDown(imKeyCode(CONTROL)));
        io.setKeyShift(io.getKeysDown(imKeyCode(SHIFT)));
        io.setKeyAlt(io.getKeysDown(imKeyCode(ALT)));
    }

    @Override
    public void keyPressed() {
        keyEvent(true);
    }

    @Override
    public void keyReleased() {
        keyEvent(false);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        ImGuiIO io = ImGui.getIO();
        io.setMouseWheel(io.getMouseWheel() + e);
    }

    @Override
    public void draw() {
        Profiler.beginMeasurements("Root");

        Profiler.push("Begin GUI frame");
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(width, height);
        io.setDeltaTime(1 / 60.0f);
        io.setMousePos(mouseX, mouseY);
        io.setMouseDown(ImGuiMouseButton.Left, leftMouse);
        io.setMouseDown(ImGuiMouseButton.Middle, middleMouse);
        io.setMouseDown(ImGuiMouseButton.Right, rightMouse);
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
