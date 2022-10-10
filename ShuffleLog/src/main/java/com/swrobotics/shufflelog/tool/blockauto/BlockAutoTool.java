package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.Tool;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.util.Cooldown;
import imgui.flag.ImGuiTableFlags;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class BlockAutoTool implements Tool {
    public static final String BLOCK_DND_ID = "AB_DRAG_BLOCK";

    private static final String MSG_QUERY_BLOCK_DEFS      = "AutoBlock:QueryBlockDefs";
    private static final String MSG_QUERY_SEQUENCES       = "AutoBlock:QuerySequences";
    private static final String MSG_GET_SEQUENCE_DATA     = "AutoBlock:GetSequenceData";
    private static final String MSG_PUBLISH_SEQUENCE_DATA = "AutoBlock:PublishSequenceData";
    private static final String MSG_DELETE_SEQUENCE       = "AutoBlock:DeleteSequence";

    private static final String MSG_BLOCK_DEFS      = "AutoBlock:BlockDefs";
    private static final String MSG_SEQUENCES       = "AutoBlock:Sequences";
    private static final String MSG_SEQUENCE_DATA   = "AutoBlock:SequenceData";
    private static final String MSG_PUBLISH_CONFIRM = "AutoBlock:PublishConfirm";
    private static final String MSG_DELETE_CONFIRM  = "AutoBlock:DeleteConfirm";

    private final MessengerClient msg;

    private final Cooldown blockDefsQueryCooldown;
    private final List<BlockCategory> categories;

    private boolean receivedCategories;
    private String activeSeqName;

    public BlockAutoTool(ShuffleLog log) {
        msg = log.getMsg();
        msg.addHandler(MSG_BLOCK_DEFS, this::onBlockDefs);

        blockDefsQueryCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);
        categories = new ArrayList<>();
        receivedCategories = false;

        activeSeqName = null;
    }

    private void onBlockDefs(String type, MessageReader reader) {
        int count = reader.readInt();
        categories.clear();
        for (int i = 0; i < count; i++) {
            categories.add(BlockCategory.read(reader));
        }
        receivedCategories = true;
    }

    private void showSequenceList() {
        text("Sequences");
        separator();

        button("Add");
    }

    private void showWorkArea() {
        if (activeSeqName == null) {
            textDisabled("No sequence open");
            return;
        }

        text(activeSeqName);
        separator();

        text("The blocks go here");
    }

    private void showPalette() {
        text("Block Palette");
        separator();

        for (BlockCategory cat : categories) {
            cat.draw();
        }
    }

    @Override
    public void process() {
        if (begin("Block Auto")) {
            if (beginTable("layout", 3, ImGuiTableFlags.BordersInner | ImGuiTableFlags.Resizable)) {
                if (!receivedCategories && blockDefsQueryCooldown.request())
                    msg.send(MSG_QUERY_BLOCK_DEFS);

                tableNextColumn();
                showSequenceList();
                tableNextColumn();
                showWorkArea();
                tableNextColumn();
                showPalette();

                endTable();
            }
        }
        end();
    }
}
