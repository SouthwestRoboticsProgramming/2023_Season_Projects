package com.swrobotics.shufflelog.tool.blockauto.part;

import com.swrobotics.messenger.client.MessageReader;
import imgui.ImGui;
import imgui.type.ImDouble;

public final class DoublePart extends ParamPart {
    public static DoublePart read(MessageReader reader) {
        return new DoublePart(reader.readDouble());
    }

    private final double def;

    public DoublePart(double def) {
        this.def = def;
    }

    @Override
    public Object getDefault() {
        return def;
    }

    private static final ImDouble temp = new ImDouble();

    @Override
    public Object edit(Object prev) {
        temp.set((double) prev);
        ImGui.setNextItemWidth(50);
        ImGui.inputDouble("", temp);
        return temp.get();
    }

    @Override
    public boolean isFrame() {
        return true;
    }
}

