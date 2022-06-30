package com.swrobotics.shufflelog.tool;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.StreamUtil;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class MessengerTool implements Tool {
    private static final int MAX_LOG_HISTORY = 100;

    private static final class LogEntry {
        String timestamp;
        String type;
    }

    private final MessengerClient msg;
    private final ImString host;
    private final ImInt port;
    private final ImString name;

    private final long startTime;
    private final List<LogEntry> log;
    private final ImString logInput;

    public MessengerTool(ShuffleLog log) {
        host = new ImString(64);
        port = new ImInt(5805);
        name = new ImString(64);

        host.set("localhost");
        name.set("ShuffleLog");

        msg = new MessengerClient(host.get(), port.get(), name.get());
        msg.addHandler("*", this::onMessage);
        log.setMsg(msg);

        this.log = new ArrayList<>();
        startTime = System.currentTimeMillis();
        logInput = new ImString(64);
    }

    private void onMessage(String type, MessageReader reader) {
        LogEntry entry = new LogEntry();
        entry.timestamp = String.valueOf((System.currentTimeMillis() - startTime) / 1000.0);;
        entry.type = type;

        log.add(entry);
        if (log.size() > MAX_LOG_HISTORY) {
            log.remove(0);
        }
    }

    private void showConnectionParams() {
        boolean changed = false;
        if (beginTable("conn_layout", 2, ImGuiTableFlags.SizingStretchProp)) {
            tableNextColumn();
            text("Host:");
            tableNextColumn();
            pushItemWidth(-1);
            changed = inputText("##conn_host", host);
            popItemWidth();

            tableNextColumn();
            text("Port:");
            tableNextColumn();
            pushItemWidth(-1);
            changed |= inputInt("##conn_port", port);
            popItemWidth();

            tableNextColumn();
            text("Name:");
            tableNextColumn();
            pushItemWidth(-1);
            changed |= inputText("##conn_name", name);
            popItemWidth();

            separator();

            tableNextColumn();
            text("Status:");
            tableNextColumn();
            boolean connected = msg.isConnected();
            if (connected) {
                pushStyleColor(ImGuiCol.Text, 0.0f, 1.0f, 0.0f, 1.0f);
                text("Connected");
            } else {
                pushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
                text("Not connected");

                Exception e = msg.getLastConnectionException();
                if (e != null && isItemHovered()) {
                    beginTooltip();
                    text(StreamUtil.getStackTrace(e));
                    endTooltip();
                }
            }
            popStyleColor();

            endTable();
        }

        if (changed)
            msg.reconnect(host.get(), port.get(), name.get());
    }

    private void showMessageLog() {
        text("Message Log:");

        float footerHeight = getStyle().getItemSpacingY() + getFrameHeightWithSpacing();
        beginChild("log_scroll", 0, -footerHeight);
        if (beginTable("log_table", 3, ImGuiTableFlags.BordersOuter | ImGuiTableFlags.BordersInnerV)) {
            for (LogEntry entry : log) {
                tableNextColumn();
                text(entry.timestamp);
                tableNextColumn();
                text(entry.type);
                tableNextColumn();
                text("Data goes here");
            }

            endTable();
        }

        // Autoscroll
        if (getScrollY() >= getScrollMaxY())
            setScrollHereY(1.0f);

        endChild();

        separator();

        boolean refocus = false;
        pushItemWidth(-1);
        if (inputText("##log_input", logInput, ImGuiInputTextFlags.EnterReturnsTrue)) {
            logInput.set("");
            refocus = true;
        }
        popItemWidth();

        setItemDefaultFocus();
        if (refocus)
            setKeyboardFocusHere(-1);
    }

    @Override
    public void process() {
        if (begin("Messenger")) {
            setWindowPos(50, 50, ImGuiCond.FirstUseEver);
            setWindowSize(500, 450, ImGuiCond.FirstUseEver);

            showConnectionParams();
            separator();
            showMessageLog();
        }
        end();
    }
}
