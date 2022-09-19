package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.Tool;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.util.Cooldown;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class BlockAutoTool implements Tool {
    private static final String MSG_QUERY_BLOCK_DEFS = "AutoBlock:QueryBlockDefs";
    private static final String MSG_QUERY_SEQUENCES  = "AutoBlock:QuerySequences";
    private static final String MSG_CREATE_SEQUENCE  = "AutoBlock:CreateSequence";
    private static final String MSG_READ_SEQUENCE    = "AutoBlock:ReadSequence";
    private static final String MSG_UPDATE_SEQUENCE  = "AutoBlock:UpdateSequence";
    private static final String MSG_DELETE_SEQUENCE  = "AutoBlock:DeleteSequence";

    private static final String MSG_BLOCK_DEFS     = "AutoBlock:BlockDefs";
    private static final String MSG_SEQUENCES      = "AutoBlock:Sequences";
    private static final String MSG_CREATE_CONFIRM = "AutoBlock:CreateConfirm";
    private static final String MSG_SEQUENCE_DATA  = "AutoBlock:SequenceData";
    private static final String MSG_UPDATE_CONFIRM = "AutoBlock:UpdateConfirm";
    private static final String MSG_DELETE_CONFIRM = "AutoBlock:DeleteConfirm";

    private final MessengerClient msg;

    private final Cooldown blockDefsQueryCooldown;
    private final List<BlockCategory> categories;

    public BlockAutoTool(ShuffleLog log) {
        msg = log.getMsg();
        msg.addHandler(MSG_BLOCK_DEFS, this::onBlockDefs);

        blockDefsQueryCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);
        categories = new ArrayList<>();
    }

    private void onBlockDefs(String type, MessageReader reader) {
        int count = reader.readInt();
        categories.clear();
        for (int i = 0; i < count; i++) {
            categories.add(BlockCategory.read(reader));
        }
    }

    @Override
    public void process() {
        if (begin("Block Auto")) {
            if (blockDefsQueryCooldown.request())
                msg.send(MSG_QUERY_BLOCK_DEFS);

            for (BlockCategory cat : categories) {
                cat.draw();
            }
        }
        end();
    }
}
