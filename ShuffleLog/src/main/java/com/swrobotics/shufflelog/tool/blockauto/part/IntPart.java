package com.swrobotics.shufflelog.tool.blockauto.part;

import com.swrobotics.messenger.client.MessageReader;
import imgui.ImGui;
import imgui.type.ImInt;

public final class IntPart extends ParamPart {
    public static IntPart read(MessageReader reader) {
        return new IntPart(reader.readInt());
    }

    private final int def;

    public IntPart(int def) {
        this.def = def;
    }

    private static final ImInt temp = new ImInt();

    @Override
    public Object getDefault() {
        return def;
    }

    @Override
    public Object edit(Object prev) {
        temp.set((int) prev);
        ImGui.setNextItemWidth(75);
        ImGui.inputInt("", temp);
        return temp.get();
    }
}
