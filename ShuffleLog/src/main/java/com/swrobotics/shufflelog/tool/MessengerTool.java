package com.swrobotics.shufflelog.tool;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.StreamUtil;
import com.swrobotics.shufflelog.tool.Tool;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.type.ImInt;
import imgui.type.ImString;

import static imgui.ImGui.*;

public final class MessengerTool implements Tool {
    private final MessengerClient msg;
    private final ImString host;
    private final ImInt port;
    private final ImString name;

    public MessengerTool(ShuffleLog log) {
        host = new ImString(64);
        port = new ImInt(5805);
        name = new ImString(64);

        host.set("localhost");
        name.set("ShuffleLog");

        msg = new MessengerClient(host.get(), port.get(), name.get());
        log.setMsg(msg);
    }

    @Override
    public void process() {
        if (begin("Messenger")) {
            setWindowPos(50, 50, ImGuiCond.FirstUseEver);
            setWindowSize(350, 175, ImGuiCond.FirstUseEver);

            boolean changed = false;
            if (beginTable("layout", 2)) {
                tableNextColumn();
                text("Host:");
                tableNextColumn();
                pushItemWidth(-1);
                changed |= inputText("##host", host);
                popItemWidth();

                tableNextColumn();
                text("Port:");
                tableNextColumn();
                pushItemWidth(-1);
                changed |= inputInt("##port", port);
                popItemWidth();

                tableNextColumn();
                text("Name:");
                tableNextColumn();
                pushItemWidth(-1);
                changed |= inputText("##name", name);
                popItemWidth();

                separator();

                tableNextColumn();
                text("Status:");
                tableNextColumn();
                boolean connected = msg.isConnected();
                if (connected) {
                    pushStyleColor(ImGuiCol.Text, 0.0f, 1.0f, 0.0f, 1.0f);
                    text("Connected");
                    popStyleColor();
                } else {
                    pushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
                    text("Not connected");

                    Exception e = msg.getLastConnectionException();
                    if (e != null && isItemHovered()) {
                        beginTooltip();
                        text(StreamUtil.getStackTrace(e));
                        endTooltip();
                    }
                    popStyleColor();
                }

                endTable();
            }

            if (changed)
                msg.reconnect(host.get(), port.get(), name.get());
        }
        end();
    }
}
