package com.swrobotics.shufflelog.tool.taskmanager;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.Tool;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class TaskLogTool implements Tool {
    private static final int MAX_LOG_HISTORY = 100;

    private final String taskName;
    private final ShuffleLog shuffleLog;
    private final ImBoolean open;

    private static final class Entry {
        final boolean isErr;
        final String line;

        public Entry(boolean isErr, String line) {
            this.isErr = isErr;
            this.line = line;
        }
    }

    private final List<Entry> log;

    public TaskLogTool(String prefix, String taskName, ShuffleLog shuffleLog) {
        this.taskName = taskName;
        this.shuffleLog = shuffleLog;
        open = new ImBoolean(false);

        shuffleLog.getMsg().addHandler(prefix + TaskManagerTool.MSG_STDOUT + taskName, this::onStdOut);
        shuffleLog.getMsg().addHandler(prefix + TaskManagerTool.MSG_STDERR + taskName, this::onStdErr);
        log = new ArrayList<>();
    }

    public void setOpen() {
        open.set(true);
    }

    public boolean isOpen() {
        return open.get();
    }

    private void addEntry(Entry entry) {
        log.add(entry);
        if (log.size() > MAX_LOG_HISTORY) {
            log.remove(0);
        }
    }

    private void onStdOut(String type, MessageReader reader) {
        addEntry(new Entry(false, reader.readString()));
    }

    private void onStdErr(String type, MessageReader reader) {
        addEntry(new Entry(true, reader.readString()));
    }

    @Override
    public void process() {
        if (begin("Task Log [" + taskName + "]", open)) {
            for (Entry entry : log) {
                if (entry.isErr)
                    pushStyleColor(ImGuiCol.Text, 1, 0, 0, 1);
                text(entry.line);
                if (entry.isErr)
                    popStyleColor();
            }

            // Autoscroll
            if (getScrollY() >= getScrollMaxY())
                setScrollHereY(1.0f);
        }
        end();

        if (!open.get()) {
            shuffleLog.removeTool(this);
            // TODO: Probably should remove handler from Messenger here
        }
    }
}
