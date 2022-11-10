package com.swrobotics.robot.blockauto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.messenger.MessageReader;
import com.swrobotics.lib.messenger.MessengerClient;
import com.swrobotics.lib.net.NTMultiSelect;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.lib.schedule.CommandLoop;
import com.swrobotics.lib.schedule.CommandUnion;
import com.swrobotics.lib.schedule.WaitCommand;
import com.swrobotics.lib.time.Duration;
import com.swrobotics.lib.time.TimeUnit;
import com.swrobotics.lib.wpilib.RobotState;
import com.swrobotics.robot.Robot;
import com.swrobotics.robot.auto.AutoDriveForTime;
import com.swrobotics.robot.auto.AutoSpinToAngle;
import com.swrobotics.robot.auto.DriveAutoInput;
import com.swrobotics.robot.blockauto.part.AnglePart;
import com.swrobotics.robot.subsystem.thrower.commands.ShootCommand;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;

public final class AutoBlocks {
    // Dropdown menu in ShuffleLog to select which auto to run
    private static final NTMultiSelect<PersistentSequence> SELECTED_AUTO = new NTMultiSelect<>("Auto/Sequence", null) {
        @Override
        protected PersistentSequence valueOf(String name) {
            return sequences.get(name);
        }

        @Override
        protected String nameOf(PersistentSequence seq) {
            return seq == null ? "[NULL]" : seq.getName();
        }
    };

    public static Command getSelectedAutoCommand() {
        PersistentSequence seq = SELECTED_AUTO.get();
        if (seq == null)
            return null;

        return seq.getStack().toCommand(robot);
    }

    private static void defineBlocks() {
        // The name parameter to newBlock("...") must be unique

        BlockCategory control = defineCategory("Control");
        control.newBlock("wait")
            .text("Wait")
            .paramDouble(1)
            .text("seconds")
            .creator((params, robot) -> new WaitCommand((double) params[0], TimeUnit.SECONDS));
        control.newBlock("repeat")
            .text("Repeat")
            .paramInt(10)
            .text("times")
            .paramBlockStack()
            .creator((params, robot) -> new CommandLoop(((BlockStackInst) params[1]).toCommand(robot), (int) params[0]));
        control.newBlock("union")
            .text("Union of")
            .paramBlockStack()
            .text("and")
            .paramBlockStack()
            .creator((params, robot) -> new CommandUnion(((BlockStackInst) params[0]).toCommand(robot), ((BlockStackInst) params[1]).toCommand(robot)));

        BlockCategory drive = defineCategory("Drive");
        drive.newBlock("drive_for_time")
                .text("Drive")
                .paramEnum(DriveAutoInput.Mode.class, DriveAutoInput.Mode.FIELD_RELATIVE)
                .newLine()
                .text("move:")
                .paramVec2d(0, 0)
                .text("(m/s), turn:")
                .paramAngle(AnglePart.Mode.CCW_RAD, 0)
                .text("(ccw rad/s)")
                .newLine()
                .text("for")
                .paramDouble(1)
                .text("seconds")
                .creator((params, robot) -> new AutoDriveForTime(
                        robot.getDrive(),
                        new DriveAutoInput((Vec2d) params[1], (Angle) params[2], (DriveAutoInput.Mode) params[0]),
                        new Duration((double) params[3], TimeUnit.SECONDS)
                ));

        drive.newBlock("spin_to_angle")
                .paramAngle(AnglePart.Mode.CCW_RAD, 0)
                .text("(ccw rad)")
                .text("robot relative:")
                .paramBoolean(false) // FIXME-Ryan: Add boolean support
                .creator((params, robot) -> new AutoSpinToAngle(
                    robot.getDrive(), 
                    (Angle) params[0], 
                    (boolean) params[1]
                ));

        BlockCategory ballFeed = defineCategory("Ball Feed");
        ballFeed.newBlock("shoot")
                .text("Shoot ball")
                .creator((params, robot) -> new ShootCommand(robot.getThrower().getHopper()));

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

    private static final File PERSISTENCE_DIR = new File(Filesystem.getOperatingDirectory(), "BlockAuto");
    private static final String PERSISTENCE_FILE_EXT = ".auto";
    private static final Map<String, PersistentSequence> sequences = new HashMap<>();

    private static final Map<String, BlockDef> blockDefRegistry = new HashMap<>();
    private static MessengerClient msg;
    private static Robot robot;

    public static BlockDef getBlockDef(String id) {
        return blockDefRegistry.get(id);
    }

    public static void init(MessengerClient msg, Robot robot) {
        defineBlocks();

        if (!PERSISTENCE_DIR.exists() && !PERSISTENCE_DIR.mkdirs()) {
            DriverStation.reportWarning("Block auto: Failed to create persistence directory", false);
        }

        // Read in existing sequences
        File[] persistenceFiles = PERSISTENCE_DIR.listFiles();
        if (persistenceFiles != null) {
            for (File file : persistenceFiles) {
                if (!file.getName().endsWith(PERSISTENCE_FILE_EXT))
                    continue;

                String name = file.getName();
                name = name.substring(0, name.length() - PERSISTENCE_FILE_EXT.length());

                PersistentSequence seq = new PersistentSequence(name, file);
                sequences.put(name, seq);
            }
        }
        updateSelectorOptions();

        AutoBlocks.msg = msg;
        AutoBlocks.robot = robot;
        msg.addHandler(MSG_QUERY_BLOCK_DEFS,      AutoBlocks::onQueryBlockDefs);
        msg.addHandler(MSG_QUERY_SEQUENCES,       AutoBlocks::onQuerySequences);
        msg.addHandler(MSG_GET_SEQUENCE_DATA,     AutoBlocks::onGetSequenceData);
        msg.addHandler(MSG_PUBLISH_SEQUENCE_DATA, AutoBlocks::onPublishSequenceData);
        msg.addHandler(MSG_DELETE_SEQUENCE,       AutoBlocks::onDeleteSequence);

        System.out.println("Block auto initialized");
    }

    private static void updateSelectorOptions() {
        SELECTED_AUTO.setOptions(new ArrayList<>(sequences.values()));
    }

    private static void onQueryBlockDefs(String type, MessageReader reader) {
        MessageBuilder builder = msg.prepare(MSG_BLOCK_DEFS);
        builder.addInt(categories.size());
        for (BlockCategory cat : categories) {
            cat.writeToMessenger(builder);
        }
        builder.send();
    }

    private static void onQuerySequences(String type, MessageReader reader) {
        MessageBuilder builder = msg.prepare(MSG_SEQUENCES);
        builder.addInt(sequences.size());
        for (String name : sequences.keySet()) {
            builder.addString(name);
        }
        builder.send();
    }

    private static void onGetSequenceData(String type, MessageReader reader) {
        String name = reader.readString();
        PersistentSequence sequence = sequences.get(name);

        MessageBuilder builder = msg.prepare(MSG_SEQUENCE_DATA);
        builder.addString(name);

        if (sequence == null) {
            builder.addBoolean(false);
            builder.send();
            return;
        }

        builder.addBoolean(true);
        sequence.getStack().write(builder);
        builder.send();
    }

    private static void onPublishSequenceData(String type, MessageReader reader) {
        String name = reader.readString();
        BlockStackInst inst = BlockStackInst.readFromMessenger(reader);

        PersistentSequence sequence = new PersistentSequence(
                name,
                new File(PERSISTENCE_DIR, name + PERSISTENCE_FILE_EXT),
                inst
        );
        sequences.put(name, sequence);
        sequence.save();
        updateSelectorOptions();

        msg.prepare(MSG_PUBLISH_CONFIRM)
                .addString(name)
                .send();
    }

    private static void onDeleteSequence(String type, MessageReader reader) {
        String name = reader.readString();

        PersistentSequence sequence = sequences.remove(name);
        boolean success = false;
        if (sequence != null) {
            success = sequence.delete();
        }
        updateSelectorOptions();

        msg.prepare(MSG_DELETE_CONFIRM)
                .addString(name)
                .addBoolean(success)
                .send();
    }
}
