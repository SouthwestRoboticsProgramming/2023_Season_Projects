package com.swrobotics.shufflelog.tool;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

import static imgui.ImGui.*;

// TODO: Move connecting and disconnecting to separate thread (pool?)
public final class NetworkTablesTool implements Tool {
    private static final int TEAM_NUMBER = 0;
    private static final int ADDRESS = 1;
    private static final String[] MODE_NAMES = {"Team Number", "Address"};

    private static final int DEFAULT_TEAM_NUMBER = 2129;
    private static final String DEFAULT_HOST = "10.21.29.2";
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

    @Override
    public void process() {
        if (begin("NetworkTables")) {
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
        end();
    }
}
