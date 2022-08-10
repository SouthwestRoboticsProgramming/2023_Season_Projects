package com.swrobotics.shufflelog.tool.scheduler;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.tool.Tool;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.util.Cooldown;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static imgui.ImGui.*;

// TODO: Finish
public final class SchedulerTool implements Tool {
    private static final String MSG_QUERY = "Scheduler:Query";
    private static final String MSG_QUERY_RESPONSE = "Scheduler:QueryResponse";
    private static final String MSG_SUBSYSTEM_ADDED = "Scheduler:SubsystemAdded";
    private static final String MSG_SUBSYSTEM_REMOVED = "Scheduler:SubsystemRemoved";
    private static final String MSG_COMMAND_ADDED = "Scheduler:CommandAdded";
    private static final String MSG_COMMAND_REMOVED = "Scheduler:CommandRemoved";

    private static final byte TYPE_CODE_SUBSYSTEM = 0;
    private static final byte TYPE_CODE_COMMAND = 1;

    private final MessengerClient msg;
    private final List<ScheduleNode> rootNodes;
    private final Map<UUID, ScheduleNode> nodesById;

    private boolean receivedInitialData;
    private final Cooldown queryCooldown;

    public SchedulerTool(MessengerClient msg) {
        this.msg = msg;
        rootNodes = new ArrayList<>();
        nodesById = new HashMap<>();

        receivedInitialData = false;
        queryCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);

        msg.addHandler(MSG_QUERY_RESPONSE, this::onQueryResponse);
    }

    private UUID readUUID(MessageReader reader) {
        long lsb = reader.readLong();
        long msb = reader.readLong();
        return new UUID(msb, lsb);
    }

    private SubsystemNode readSubsystemNode(MessageReader reader) {
        SubsystemNode node = new SubsystemNode();

        int childCount = reader.readInt();
        for (int i = 0; i < childCount; i++) {
            node.addChild(readNode(reader));
        }

        return node;
    }

    private CommandNode readCommandNode(MessageReader reader) {
        CommandNode node = new CommandNode();

        // TODO: Read interval

        return node;
    }

    private ScheduleNode readNode(MessageReader reader) {
        UUID id = readUUID(reader);
        String typeName = reader.readString();

        byte typeCode = reader.readByte();
        ScheduleNode node;
        switch (typeCode) {
            case TYPE_CODE_SUBSYSTEM:
                node = readSubsystemNode(reader);
                break;
            case TYPE_CODE_COMMAND:
                node = readCommandNode(reader);
                break;
            default:
                throw new RuntimeException("Invalid type code");
        }

        node.setTypeName(typeName);
        node.setId(id);
        nodesById.put(id, node);

        return node;
    }

    private void onQueryResponse(String type, MessageReader reader) {
        receivedInitialData = true;

        int entryCount = reader.readInt();
        rootNodes.clear();
        for (int i = 0; i < entryCount; i++) {
            rootNodes.add(readNode(reader));
        }
    }

    private void showNode(ScheduleNode node) {
        if (node instanceof SubsystemNode) {
            SubsystemNode ss = (SubsystemNode) node;

            tableNextColumn();
            boolean open = treeNodeEx(ss.getTypeName() + "##" + ss.getId(), ss.getChildren().isEmpty() ? ImGuiTreeNodeFlags.Leaf : ImGuiTreeNodeFlags.None);
            tableNextColumn();
            textDisabled(ss.getId().toString());

            if (open) {
                for (ScheduleNode child : ss.getChildren())
                    showNode(child);

                treePop();
            }
        } else if (node instanceof CommandNode) {
            CommandNode cmd = (CommandNode) node;

            tableNextColumn();
            text(cmd.getTypeName());
            tableNextColumn();
            textDisabled(cmd.getId().toString());
        }
    }

    @Override
    public void process() {
        if (begin("Scheduler Tool")) {
            if (!receivedInitialData && queryCooldown.request())
                msg.send(MSG_QUERY);

            if (button("Refresh"))
                msg.send(MSG_QUERY);

            if (beginTable("nodes", 2, ImGuiTableFlags.Borders | ImGuiTableFlags.Resizable)) {
                for (ScheduleNode node : rootNodes)
                    showNode(node);

                endTable();
            }
        }
        end();
    }
}
