package com.swrobotics.shufflelog.tool.messenger;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.StreamUtil;
import com.swrobotics.shufflelog.tool.Tool;
import com.swrobotics.shufflelog.tool.messenger.data.MessageDataElementFormat;
import com.swrobotics.shufflelog.tool.messenger.data.MessageDataElementInstance;
import com.swrobotics.shufflelog.tool.messenger.data.MessageDataElementType;
import com.swrobotics.shufflelog.tool.messenger.data.MessageDataFormat;
import com.swrobotics.shufflelog.tool.messenger.data.MessageDataInstance;
import com.swrobotics.shufflelog.tool.messenger.decodeResult.ExceptionDecodeResult;
import com.swrobotics.shufflelog.tool.messenger.decodeResult.MessageDataDecodeResult;
import com.swrobotics.shufflelog.tool.messenger.decodeResult.NoKnownFormatDecodeResult;
import com.swrobotics.shufflelog.tool.messenger.decodeResult.SuccessfulDecodeResult;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static imgui.ImGui.*;

public final class MessengerTool implements Tool {
    private static final int MAX_LOG_HISTORY = 500;
    private static final String FORMAT_EDIT_TITLE = "Edit Format";

    private static final class LogEntry {
        String timestamp;
        String type;
        byte[] rawData;
        MessageDataDecodeResult decodeResult;

        int index;
        boolean showData;
    }

    private final MessengerClient msg;
    private final ImString host;
    private final ImInt port;
    private final ImString name;

    private final long startTime;
    private final List<LogEntry> log;
    private final ImString logFilter;
    private final ImBoolean popupOpen;
    private int nextEntryIndex;

    private final Map<String, MessageDataFormat> formats;
    private String editingFormatType;
    private MessageDataFormat editingFormat;
    private int editingEntryIndex;

    public MessengerTool(ShuffleLog log) {
        host = new ImString(64);
        port = new ImInt(5805);
        name = new ImString(64);
        popupOpen = new ImBoolean(true);

        host.set("localhost");
        name.set("ShuffleLog");

        msg = new MessengerClient(host.get(), port.get(), name.get());
        msg.addHandler("*", this::onMessage);
        log.setMsg(msg);

        this.log = new ArrayList<>();
        startTime = System.currentTimeMillis();
        logFilter = new ImString(64);
        nextEntryIndex = 0;

        formats = new HashMap<>();

        // Messenger:Event format is defined by default, since it is standard and always present
        MessageDataFormat format = new MessageDataFormat();
        format.addElement(new MessageDataElementFormat("Event Type", MessageDataElementType.STRING));
        format.addElement(new MessageDataElementFormat("Client Name", MessageDataElementType.STRING));
        format.addElement(new MessageDataElementFormat("Descriptor", MessageDataElementType.STRING));
        format.setReadOnly();
        formats.put("Messenger:Event", format);
    }

    private void decodeMessageData(LogEntry entry) {
        MessageDataFormat format = formats.get(entry.type);
        if (format != null) {
            try {
                MessageReader readerCopy = new MessageReader(entry.rawData);
                MessageDataInstance data = format.decode(readerCopy);
                readerCopy.close();
                entry.decodeResult = new SuccessfulDecodeResult(data);
            } catch (Throwable t) {
                entry.decodeResult = new ExceptionDecodeResult(t);
            }
        } else {
            entry.decodeResult = new NoKnownFormatDecodeResult(entry.rawData);
        }
    }

    private void onMessage(String type, MessageReader reader) {
        LogEntry entry = new LogEntry();
        entry.timestamp = String.valueOf((System.currentTimeMillis() - startTime) / 1000.0);
        entry.type = type;
        entry.index = nextEntryIndex++;
        entry.showData = false;

        entry.rawData = reader.readAllData();
        decodeMessageData(entry);

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

        boolean continueTable = false;

        int flags = ImGuiTableFlags.BordersV
                | ImGuiTableFlags.BordersOuterH
                | ImGuiTableFlags.Resizable;

        byte[] filterData = logFilter.getData();
        StringBuilder filter = new StringBuilder();
        for (byte b : filterData) {
            if (b == 0)
                break;
            else
                filter.append((char) b);
        }

        for (LogEntry entry : log) {
            // Filter out entries that don't match
            if (!entry.type.toLowerCase().contains(filter.toString().toLowerCase()))
                continue;

            if (continueTable || beginTable("log_table", 3, flags | ImGuiTableFlags.RowBg)) {
                tableNextColumn();
                alignTextToFramePadding();
                text(entry.timestamp);
                tableNextColumn();
                alignTextToFramePadding();
                text(entry.type);
                tableNextColumn();
                setNextItemWidth(-1);
                if (arrowButton(String.valueOf(entry.index), entry.showData ? ImGuiDir.Down : ImGuiDir.Right)) {
                    entry.showData = !entry.showData;
                }

                continueTable = !entry.showData;
                if (entry.showData) {
                    endTable();

                    if (entry.decodeResult instanceof NoKnownFormatDecodeResult) {
                        text("Data: " + ((NoKnownFormatDecodeResult) entry.decodeResult).getDataHex());
                        text("No format known");
                        pushID(entry.index);
                        if (button("Define a format...")) {
                            editingFormat = new MessageDataFormat();
                            editingFormatType = entry.type;
                            editingEntryIndex = entry.index;
                            openPopup(FORMAT_EDIT_TITLE);
                        }
                        popID();
                    } else if (entry.decodeResult instanceof ExceptionDecodeResult) {
                        pushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
                        text(StreamUtil.getStackTrace(((ExceptionDecodeResult) entry.decodeResult).getException()));
                        popStyleColor();

                        MessageDataFormat format = formats.get(entry.type);
                        pushID(entry.index);
                        if (!format.isReadOnly() && button("Edit format...")) {
                            editingFormatType = entry.type;
                            editingFormat = format.copy();
                            editingEntryIndex = entry.index;
                            openPopup(FORMAT_EDIT_TITLE);
                        }
                        popID();
                    } else if (entry.decodeResult instanceof SuccessfulDecodeResult) {
                        MessageDataInstance data = ((SuccessfulDecodeResult) entry.decodeResult).getData();

                        if (beginTable("log_data", 2, flags)) {
                            for (MessageDataElementInstance<?> element : data.getElements()) {
                                tableNextColumn();
                                text(element.getName());
                                tableNextColumn();
                                text(String.valueOf(element.getValue()));
                            }
                            endTable();
                        }

                        MessageDataFormat format = formats.get(entry.type);
                        pushID(entry.index);
                        if (!format.isReadOnly() && button("Edit format...")) {
                            editingFormatType = entry.type;
                            editingFormat = format.copy();
                            editingEntryIndex = entry.index;
                            openPopup(FORMAT_EDIT_TITLE);
                        }
                        popID();
                    }

                    if (entry.index == editingEntryIndex) {
                        pushID(entry.index);
                        showMessageDataFormatEditPopup();
                        popID();
                    }
                }
            }
        }
        if (continueTable)
            endTable();

        // Autoscroll
        if (getScrollY() >= getScrollMaxY())
            setScrollHereY(1.0f);

        endChild();

        separator();

        boolean refocus = false;
        alignTextToFramePadding();
        text("Filter:");
        sameLine();
        pushItemWidth(-1);
        if (inputText("##log_filter", logFilter, ImGuiInputTextFlags.EnterReturnsTrue)) {
            //logFilter.set("");
            refocus = true;
        }
        popItemWidth();

        setItemDefaultFocus();
        if (refocus)
            setKeyboardFocusHere(-1);
    }

    private void updateAndDecodeFormat() {
        formats.put(editingFormatType, editingFormat);

        // Re-decode all entries affected by the format
        for (LogEntry entry : log) {
            if (entry.type.equals(editingFormatType)) {
                decodeMessageData(entry);
            }
        }
    }

    private void showMessageDataFormatEditPopup() {
        ImVec2 center = getMainViewport().getCenter();
        setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);

        if (beginPopupModal(FORMAT_EDIT_TITLE, ImGuiWindowFlags.AlwaysAutoResize)) {
            // Make the window wider
            dummy(400, 0);

            text("Editing format for " + editingFormatType);
            separator();

            int flags = ImGuiTableFlags.BordersV
                    | ImGuiTableFlags.BordersOuterH
                    | ImGuiTableFlags.Resizable
                    | ImGuiTableFlags.RowBg;

            if (editingFormat.getElementFormats().isEmpty()) {
                text("No entries");
            } else {
                int i = 0;
                if (beginTable("format", 3, flags)) {
                    MessageDataElementFormat removed = null;
                    for (MessageDataElementFormat element : editingFormat.getElementFormats()) {
                        ImString nameStr = new ImString(64);
                        nameStr.set(element.getName());

                        tableNextColumn();
                        setNextItemWidth(-1);
                        inputText("##name_" + i, nameStr);
                        tableNextColumn();

                        setNextItemWidth(-1);
                        if (beginCombo("##type_" + i, element.getType().name().toLowerCase())) {
                            for (MessageDataElementType type : MessageDataElementType.values()) {
                                boolean selected = type == element.getType();
                                if (selectable(type.name().toLowerCase(), selected))
                                    element.setType(type);

                                if (selected)
                                    setItemDefaultFocus();
                            }
                            endCombo();
                        }

                        tableNextColumn();
                        setNextItemWidth(-1);
                        if (button("Remove##" + i)) {
                            removed = element;
                        }

                        element.setName(nameStr.get());

                        i++;
                    }
                    endTable();

                    if (removed != null)
                        editingFormat.removeElement(removed);
                }
            }

            if (button("Add")) {
                editingFormat.getElementFormats().add(new MessageDataElementFormat("Element", MessageDataElementType.values()[0]));
            }

            separator();
            if (button("Cancel"))
                closeCurrentPopup();
            sameLine();
            if (button("Confirm")) {
                updateAndDecodeFormat();
                closeCurrentPopup();
            }

            endPopup();
        }
    }

    @Override
    public void process() {
        if (begin("Messenger")) {
            setWindowPos(50, 50, ImGuiCond.FirstUseEver);
            setWindowSize(500, 450, ImGuiCond.FirstUseEver);

            showConnectionParams();
            separator();
            showMessageLog();
            showMessageDataFormatEditPopup();
        }
        end();
    }
}
