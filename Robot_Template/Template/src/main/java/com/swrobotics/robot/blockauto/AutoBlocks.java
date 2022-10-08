package com.swrobotics.robot.blockauto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;
import com.team2129.lib.messenger.MessengerClient;
import com.team2129.lib.schedule.Command;
import com.team2129.lib.schedule.CommandLoop;
import com.team2129.lib.schedule.CommandUnion;
import com.team2129.lib.schedule.WaitCommand;
import com.team2129.lib.time.TimeUnit;

import edu.wpi.first.wpilibj.Filesystem;

public final class AutoBlocks {
    private static void defineBlocks() {
        // The name parameter to newBlock("...") must be unique

        BlockCategory control = defineCategory("Control");
        control.newBlock("wait")
            .text("Wait")
            .paramDouble(1)
            .text("seconds")
            .creator((params) -> new WaitCommand((double) params[0], TimeUnit.SECONDS));
        control.newBlock("repeat")
            .text("Repeat")
            .paramInt(10)
            .text("times")
            .paramBlockStack()
            .creator((params) -> new CommandLoop(((BlockStackInst) params[1]).toCommand(), (int) params[0]));
        control.newBlock("union")
            .text("Union")
            .paramBlockStack()
            .paramBlockStack()
            .creator((params) -> new CommandUnion(((BlockStackInst) params[0]).toCommand(), ((BlockStackInst) params[1]).toCommand()));

        // BlockCategory drive = defineCategory("Drive");
        // drive.newBlock("drive_point")
        //     .text("Drive to")
        //     .paramFieldPoint(0, 0)
        //     .creator((params) -> new DriveToPointCommand((Vec2d) params[0]));
        // drive.newBlock("turn_angle")
        //     .text("Turn to")
        //     .paramAngle(AnglePart.Mode.CW_DEG, 0)
        //     .creator((params) -> new TurnToAngleCommand((Angle) params[0]));

        // BlockCategory intake = defineCategory("Intake");
        // intake.newBlock("intake_set")
        //     .text("Set intake mode to")
        //     .paramEnum(Intake.Mode.class, Intake.Mode.OFF)
        //     .creator((params) -> new IntakeSetModeCommand((Intake.Mode) params[0]));

        // BlockCategory ballFeed = defineCategory("Ball Feed");
        // ballFeed.newBlock("shoot")
        //     .text("Shoot ball")
        //     .creator((params) -> new ShootBallCommand());
        // ballFeed.newBlock("eject")
        //     .text("Eject ball")
        //     .creator((params) -> new EjectBallCommand());

        initRegistryAndValidate();
    }

    private static final List<BlockCategory> categories = new ArrayList<>();

    private static BlockCategory defineCategory(String name) {
        BlockCategory cat = new BlockCategory(name);
        categories.add(cat);
        return cat;
    }

    private static void initRegistryAndValidate() {
        for (BlockCategory cat : categories) {
            for (BlockDef block : cat.getBlocks()) {
                block.validate();

                if (blockDefRegistry.containsKey(block.getName()))
                    throw new IllegalStateException("Block definition validation failed: Duplicate name '" + block.getName() + "'");

                blockDefRegistry.put(block.getName(), block);
            }
        }
    }

    /*
     * Protocol:
     * 
     * S->R: Query block definitions
     * R->S: Block definitions (categories)
     * 
     * S->R: Query sequences
     * R->S: Sequences (string list)
     * 
     * S->R: Create sequence (string)
     * R->S: Create confirm (same string)
     * S->R: Delete sequence (string)
     * R->S: Delete confirm (same string)
     * S->R: Get sequence data (string)
     * R->S: Sequence data (block stack inst)
     * S->R: Publish sequence data (block stack inst)
     * R->S: Confirm publish
     * 
     * Storage:
     * 
     * Auto sequences are stored in the same format as the Messenger data to simplify code.
     * Stored in .auto files.
     */

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

    private static final File PERSISTENCE_DIR = new File(Filesystem.getOperatingDirectory(), "BlockAuto");
    private static final String PERSISTENCE_FILE_EXT = ".auto";
    private static final Map<String, PersistentSequence> sequences = new HashMap<>();

    private static final Map<String, BlockDef> blockDefRegistry = new HashMap<>();
    private static MessengerClient msg;

    public static BlockDef getBlockDef(String id) {
        return blockDefRegistry.get(id);
    }

    public static void init(MessengerClient msg) {
        defineBlocks();

        if (!PERSISTENCE_DIR.exists())
            PERSISTENCE_DIR.mkdirs();

        // test
        BlockStackInst stack = new BlockStackInst();
        stack.addBlock(new BlockInst(blockDefRegistry.get("wait"), 1.0));
        BlockStackInst stack2 = new BlockStackInst();
        stack2.addBlock(new BlockInst(blockDefRegistry.get("wait"), 10.0));
        stack.addBlock(new BlockInst(blockDefRegistry.get("repeat"), 10, stack2));
        PersistentSequence s = new PersistentSequence(new File(PERSISTENCE_DIR, "wait.auto"), stack);
        s.save();

        // Read in existing sequences
        for (File file : PERSISTENCE_DIR.listFiles()) {
            if (!file.getName().endsWith(PERSISTENCE_FILE_EXT))
                continue;

            String name = file.getName();
            name = name.substring(0, name.length() - PERSISTENCE_FILE_EXT.length());
    
            PersistentSequence seq = new PersistentSequence(file);
            sequences.put(name, seq);
        }

        AutoBlocks.msg = msg;
        msg.addHandler(MSG_QUERY_BLOCK_DEFS, AutoBlocks::onQueryBlockDefs);

        System.out.println("Block auto initialized");
    }

    private static void onQueryBlockDefs(String type, MessageReader reader) {
        MessageBuilder builder = msg.prepare(MSG_BLOCK_DEFS);
        builder.addInt(categories.size());
        for (BlockCategory cat : categories) {
            cat.writeToMessenger(builder);
        }
        builder.send();
    }
}
