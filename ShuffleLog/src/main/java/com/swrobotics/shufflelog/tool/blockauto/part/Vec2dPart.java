package com.swrobotics.shufflelog.tool.blockauto.part;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.tool.blockauto.BlockAutoTool;
import com.swrobotics.shufflelog.util.Vec2d;
import imgui.ImGui;
import imgui.type.ImDouble;

public class Vec2dPart extends ParamPart {
    public static Vec2dPart read(MessageReader reader) {
        double x = reader.readDouble();
        double y = reader.readDouble();
        return new Vec2dPart(x, y);
    }

    private final double defX, defY;

    public Vec2dPart(double defX, double defY) {
        this.defX = defX;
        this.defY = defY;
    }

    @Override
    public Object getDefault() {
        return new Vec2d(defX, defY);
    }

    private static final ImDouble temp = new ImDouble();

    @Override
    public Object edit(Object prev) {
        Vec2d v = (Vec2d) prev;
        temp.set(v.x);
        ImGui.setNextItemWidth(50);
        ImGui.inputDouble("##x", temp);
        v.x = temp.get();
        temp.set(v.y);
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        ImGui.inputDouble("##y", temp);
        v.y = temp.get();
        return v;
    }

    @Override
    public Object readInst(MessageReader reader, BlockAutoTool tool) {
        return new Vec2d(reader.readDouble(), reader.readDouble());
    }

    @Override
    public boolean isFrame() {
        return true;
    }
}
