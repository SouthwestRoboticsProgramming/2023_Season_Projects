package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.Tool;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.util.Cooldown;
import imgui.flag.ImGuiTableFlags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<String, BlockDef> blockDefs;

    private final Cooldown sequencesQueryCooldown;
    private final List<String> sequences;
    private boolean receivedSequences;

    private final Cooldown sequenceDataQueryCooldown;

    private String activeSeqName;
    private BlockStackInst activeSeqStack;

    public BlockAutoTool(ShuffleLog log) {
        msg = log.getMsg();
        msg.addHandler(MSG_BLOCK_DEFS, this::onBlockDefs);
        msg.addHandler(MSG_SEQUENCES, this::onSequences);
        msg.addHandler(MSG_SEQUENCE_DATA, this::onSequenceData);

        blockDefsQueryCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);
        categories = new ArrayList<>();
        receivedCategories = false;
        blockDefs = new HashMap<>();

        sequencesQueryCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);
        sequences = new ArrayList<>();
        receivedSequences = false;

        sequenceDataQueryCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);

        activeSeqName = null;
        activeSeqStack = null;
    }

    private void onBlockDefs(String type, MessageReader reader) {
        int count = reader.readInt();
        categories.clear();
        for (int i = 0; i < count; i++) {
            BlockCategory cat = BlockCategory.read(reader);
            for (BlockInst inst : cat.getBlocks()) {
                BlockDef def = inst.getDef();
                blockDefs.put(def.getName(), def);
            }
            categories.add(cat);
        }
        receivedCategories = true;
    }

    private void onSequences(String type, MessageReader reader) {
        int len = reader.readInt();
        sequences.clear();
        for (int i = 0; i < len; i++) {
            sequences.add(reader.readString());
        }
        receivedSequences = true;
    }

    private void onSequenceData(String type, MessageReader reader) {
        String name = reader.readString();
        if (!name.equals(activeSeqName)) return;

        boolean valid = reader.readBoolean();
        if (!valid) {
            System.err.println("Block auto: Invalid sequence data");
            return;
        }

        activeSeqStack = BlockStackInst.read(reader, this);
    }

    public BlockDef getBlockDef(String name) {
        return blockDefs.get(name);
    }

    private void switchSequence(String sequence) {
        activeSeqName = sequence;
        activeSeqStack = null;
    }

    private void showSequenceList() {
        text("Sequences");
        separator();

        for (String sequence : sequences) {
            if (selectable(sequence, sequence.equals(activeSeqName))) {
                switchSequence(sequence);
            }
        }

        button("Add");
    }

    private void showWorkArea() {
        if (activeSeqName == null) {
            textDisabled("No sequence open");
            return;
        }

        text(activeSeqName);
        separator();

        if (activeSeqStack == null) {
            textDisabled("Loading blocks...");
        } else {
            activeSeqStack.show();
        }
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
                if (!receivedSequences && sequencesQueryCooldown.request())
                    msg.send(MSG_QUERY_SEQUENCES);
                if (activeSeqName != null && activeSeqStack == null && sequenceDataQueryCooldown.request())
                    msg.prepare(MSG_GET_SEQUENCE_DATA).addString(activeSeqName).send();

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
