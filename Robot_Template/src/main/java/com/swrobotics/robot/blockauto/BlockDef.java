package com.swrobotics.robot.blockauto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.swrobotics.messenger.client.MessageBuilder;
import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.robot.Robot;
import com.swrobotics.robot.blockauto.part.*;
import com.team2129.lib.schedule.Command;

public final class BlockDef {
    private final String name;
    private final List<BlockPart> parts;
    private BiFunction<Object[], Robot, Command> creator;

    public BlockDef(String name) {
        this.name = name;
        parts = new ArrayList<>();
    }

    public BlockDef text(String text) {
        parts.add(new TextPart(text));
        return this;
    }

    public BlockDef newLine() {
        parts.add(NewLinePart.INSTANCE);
        return this;
    }

    public BlockDef paramInt(int def) {
        parts.add(new IntPart(def));
        return this;
    }

    public BlockDef paramDouble(double def) {
        parts.add(new DoublePart(def));
        return this;
    }

    public BlockDef paramAngle(AnglePart.Mode mode, double def) {
        parts.add(new AnglePart(mode, def));
        return this;
    }

    public BlockDef paramVec2d(double defX, double defY) {
        parts.add(new Vec2dPart(defX, defY));
        return this;
    }

    public BlockDef paramFieldPoint(double defX, double defY) {
        parts.add(new FieldPointPart(defX, defY));
        return this;
    }

    public BlockDef paramBlockStack() {
        parts.add(new BlockStackPart());
        return this;
    }

    public <E extends Enum<E>> BlockDef paramEnum(Class<E> type, E def) {
        parts.add(new EnumPart<>(type, def));
        return this;
    }

    public void creator(BiFunction<Object[], Robot, Command> creator) {
        this.creator = creator;
    }

    public void validate() {
        if (creator == null) {
            throw new IllegalStateException("Block definition validation failed: No command creator registered");
        }
    }

    public BlockInst readInstance(MessageReader reader) {
        List<Object> params = new ArrayList<>();
        for (BlockPart part : parts) {
            if (part instanceof ParamPart) {
                ParamPart p = (ParamPart) part;
                params.add(p.readInst(reader));
            }
        }
        return new BlockInst(this, params.toArray());
    }

    public void writeToMessenger(MessageBuilder builder) {
        builder.addString(name);
        builder.addInt(parts.size());
        for (BlockPart part : parts) {
            part.writeToMessenger(builder);
        }
    }

    public List<BlockPart> getParts() {
        return parts;
    }

    public String getName() {
        return name;
    }

    public BiFunction<Object[], Robot, Command> getCreator() {
        return creator;
    }
}
