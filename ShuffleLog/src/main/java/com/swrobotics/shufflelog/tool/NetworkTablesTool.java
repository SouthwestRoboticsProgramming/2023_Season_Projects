package com.swrobotics.shufflelog.tool;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImDouble;
import imgui.type.ImInt;
import imgui.type.ImString;

import static imgui.ImGui.*;

// TODO: Move connecting and disconnecting to separate thread (pool?)
public final class NetworkTablesTool implements Tool {
    private static final int TEAM_NUMBER = 0;
    private static final int ADDRESS = 1;
    private static final String[] MODE_NAMES = {"Team Number", "Address"};

    private static final int DEFAULT_TEAM_NUMBER = 2129;
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = NetworkTableInstance.kDefaultPort;

    private final NetworkTableInstance nt;

    private final ImInt connectionMode;
    private final ImString host;
    private final ImInt portOrTeamNumber;

    public NetworkTablesTool() {
        nt = NetworkTableInstance.getDefault();
        connectionMode = new ImInt(TEAM_NUMBER);

        host = new ImString(64);
        portOrTeamNumber = new ImInt(2129);

        connectNT();
    }

    private void connectNT() {
        if (connectionMode.get() == ADDRESS) {
            nt.setServer(host.get(), portOrTeamNumber.get());
        } else {
            nt.setServerTeam(portOrTeamNumber.get());
        }
        nt.setNetworkIdentity("ShuffleLog");
        nt.startClient();
        nt.startDSClient();
    }

    private void disconnectNT() {
        nt.stopDSClient();
        nt.stopClient();
        while (nt.getNetworkMode() != 0) {
            Thread.onSpinWait();
        }
        for (NetworkTableEntry entry : nt.getEntries("", 0)) {
            entry.delete();
        }
    }

    private void showConnectionParams() {
        boolean paramsChanged = false;

        text("Connection Mode:");
        if (combo("##conn_mode", connectionMode, MODE_NAMES)) {
            paramsChanged = true;

            if (connectionMode.get() == TEAM_NUMBER) {
                portOrTeamNumber.set(DEFAULT_TEAM_NUMBER);
            } else {
                host.set(DEFAULT_HOST);
                portOrTeamNumber.set(DEFAULT_PORT);
            }
        }

        separator();

        if (beginTable("layout", 2, ImGuiTableFlags.SizingStretchProp)) {
            if (connectionMode.get() == TEAM_NUMBER) {
                tableNextColumn();
                text("Team: ");
                tableNextColumn();
                pushItemWidth(-1);
                paramsChanged |= inputInt("##team", portOrTeamNumber);
                popItemWidth();
            } else {
                tableNextColumn();
                text("Host:");
                tableNextColumn();
                pushItemWidth(-1);
                paramsChanged |= inputText("##host", host);
                popItemWidth();

                tableNextColumn();
                text("Port: ");
                tableNextColumn();
                pushItemWidth(-1);
                paramsChanged |= inputInt("##port", portOrTeamNumber);
                popItemWidth();
            }

            separator();

            tableNextColumn();
            text("Status:");
            tableNextColumn();
            boolean connected = nt.isConnected();
            if (connected) {
                pushStyleColor(ImGuiCol.Text, 0.0f, 1.0f, 0.0f, 1.0f);
                text("Connected");
            } else {
                pushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
                text("Not connected");
            }
            popStyleColor();

            endTable();
        }

        if (paramsChanged) {
            disconnectNT();
            connectNT();
        }
    }

    private final ImBoolean b = new ImBoolean();
    private final ImDouble d = new ImDouble();
    private final ImString s = new ImString(256);

    private void showPrimitiveEntry(String name, NetworkTableEntry entry) {
        tableNextColumn();
        treeNodeEx(name, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);
        tableNextColumn();
        pushID("nt_value:" + name);
        switch (entry.getType()) {
            case kBoolean: {
                b.set(entry.getBoolean(false));
                if (checkbox("", b)) {
                    entry.setBoolean(b.get());
                }
                break;
            }
            case kDouble: {
                d.set(entry.getDouble(0.0));
                if (dragScalar("", ImGuiDataType.Double, d, 0.1f)) {
                    entry.setDouble(d.get());
                }
                break;
            }
            case kString: {
                s.set(entry.getString(""));
                if (inputText("", s)) {
                    entry.setString(s.get());
                }
                break;
            }
            default: {
                textDisabled("Can't edit");
            }
        }
        popID();
        tableNextColumn();
        text(entry.getType().name());
    }

    private static final boolean[] EMPTY_BOOL_ARRAY = new boolean[0];
    private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private void showArrayEntry(String name, NetworkTableEntry entry) {
        tableNextColumn();
        boolean open = treeNodeEx(name, ImGuiTreeNodeFlags.SpanFullWidth);
        tableNextColumn();
        textDisabled("--");
        tableNextColumn();
        text(entry.getType().name());

        if (open) {
            switch (entry.getType()) {
                case kBooleanArray: {
                    boolean[] val = entry.getBooleanArray(EMPTY_BOOL_ARRAY);
                    boolean changed = false;
                    for (int i = 0; i < val.length; i++) {
                        tableNextColumn();
                        treeNodeEx(String.valueOf(i), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);
                        tableNextColumn();
                        b.set(val[i]);
                        pushItemWidth(-1);
                        changed |= checkbox("##nt_elem:" + i, b);
                        popItemWidth();
                        tableNextColumn();
                        textDisabled("--");
                        val[i] = b.get();
                    }
                    if (changed) entry.setBooleanArray(val);
                    break;
                }
                case kDoubleArray: {
                    double[] val = entry.getDoubleArray(EMPTY_DOUBLE_ARRAY);
                    boolean changed = false;
                    for (int i = 0; i < val.length; i++) {
                        tableNextColumn();
                        treeNodeEx(String.valueOf(i), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);
                        tableNextColumn();
                        d.set(val[i]);
                        pushItemWidth(-1);
                        changed |= dragScalar("##nt_elem:" + i, ImGuiDataType.Double, d, 0.1f);
                        popItemWidth();
                        tableNextColumn();
                        textDisabled("--");
                        val[i] = d.get();
                    }
                    if (changed) entry.setDoubleArray(val);
                    break;
                }
                case kStringArray: {
                    String[] val = entry.getStringArray(EMPTY_STRING_ARRAY);
                    boolean changed = false;
                    for (int i = 0; i < val.length; i++) {
                        tableNextColumn();
                        treeNodeEx(String.valueOf(i), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);
                        tableNextColumn();
                        s.set(val[i]);
                        pushItemWidth(-1);
                        changed |= inputText("##nt_elem:" + i, s);
                        popItemWidth();
                        tableNextColumn();
                        textDisabled("--");
                        val[i] = s.get();
                    }
                    if (changed) entry.setStringArray(val);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown array type");
                }
            }
            treePop();
        }
    }

    private void showEntry(String name, NetworkTableEntry entry) {
        tableNextRow();

        NetworkTableType type = entry.getType();
        boolean isArray =
                type == NetworkTableType.kBooleanArray ||
                type == NetworkTableType.kDoubleArray ||
                type == NetworkTableType.kStringArray;

        if (isArray) {
            showArrayEntry(name, entry);
        } else {
            showPrimitiveEntry(name, entry);
        }
    }

    private void showTable(String name, NetworkTable table) {
        tableNextRow();

        tableNextColumn();
        boolean open = treeNodeEx(name, ImGuiTreeNodeFlags.SpanFullWidth);
        tableNextColumn();
        textDisabled("--");
        tableNextColumn();
        textDisabled("--");

        if (open) {
            for (String key : table.getKeys()) {
                showEntry(key, table.getEntry(key));
            }

            for (String subtable : table.getSubTables()) {
                showTable(subtable, table.getSubTable(subtable));
            }

            treePop();
        }
    }

    private void showEntryTree() {
        int flags = ImGuiTableFlags.BordersV
                | ImGuiTableFlags.BordersOuterH
                | ImGuiTableFlags.Resizable
                | ImGuiTableFlags.RowBg;

        if (beginTable("nt_tree", 3, flags)) {
            ImVec2 size = new ImVec2();
            calcTextSize(size, "A");
            float w = size.x;

            tableSetupColumn("Name", ImGuiTableColumnFlags.WidthFixed, w * 10);
            tableSetupColumn("Value", ImGuiTableColumnFlags.WidthStretch);
            tableSetupColumn("Type", ImGuiTableColumnFlags.WidthFixed, w * 10);
            tableHeadersRow();

            showTable("Root", nt.getTable("/"));

            endTable();
        }
    }

    @Override
    public void process() {
        if (begin("NetworkTables")) {
            showConnectionParams();
            separator();
            showEntryTree();
        }
        end();
    }
}
