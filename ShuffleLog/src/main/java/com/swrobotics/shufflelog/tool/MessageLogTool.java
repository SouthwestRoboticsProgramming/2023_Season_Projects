package com.swrobotics.shufflelog.tool;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.ShuffleLog;
import imgui.ImVec2;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class MessageLogTool implements Tool {
    private static final int MAX_HISTORY = 100;

    private final long startTime;
    private final List<Entry> history;
    private final ImString input;

    public MessageLogTool(ShuffleLog log) {
        log.getMsg().addHandler("*", this::onMessage);
        history = new ArrayList<>();

        input = new ImString(64);

        startTime = System.currentTimeMillis();
    }

    private void onMessage(String type, MessageReader reader) {
        Entry entry = new Entry();
        entry.timestamp = String.valueOf((System.currentTimeMillis() - startTime) / 1000.0);;
        entry.type = type;

        history.add(entry);
        if (history.size() > MAX_HISTORY) {
            history.remove(0);
        }
    }

    @Override
    public void process() {
        if (begin("Message Log")) {
            text("This is the message log.");

            float footerHeight = getStyle().getItemSpacingY() + getFrameHeightWithSpacing();
            beginChild("scroll", 0, -footerHeight);
            if (beginTable("log", 3, ImGuiTableFlags.BordersOuter | ImGuiTableFlags.BordersInnerV)) {
                for (Entry entry : history) {
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
            if (inputText("##input", input, ImGuiInputTextFlags.EnterReturnsTrue)) {
                System.out.println("It change");
                input.set("");
                refocus = true;
            }
            popItemWidth();

            setItemDefaultFocus();
            if (refocus)
                setKeyboardFocusHere(-1);
        }
        end();
    }

    private static final class Entry {
        String timestamp;
        String type;
    }
}
