package com.swrobotics.shufflelog.tool.blockauto.part;

import com.swrobotics.messenger.client.MessageReader;
import imgui.ImGui;
import imgui.type.ImInt;

public final class EnumPart extends ParamPart {
    public static EnumPart read(MessageReader reader) {
        int count = reader.readInt();
        String[] values = new String[count];
        for (int i = 0; i < count; i++) {
            values[i] = reader.readString();
        }
        int defIdx = reader.readInt();

        return new EnumPart(values, defIdx);
    }

    private final String[] values;
    private final int defIdx;

    public EnumPart(String[] values, int defIdx) {
        this.values = values;
        this.defIdx = defIdx;
    }

    @Override
    public Object getDefault() {
        return defIdx;
    }

    private static final ImInt temp = new ImInt();

    @Override
    public Object edit(Object prev) {
        temp.set((int) prev);
        ImGui.setNextItemWidth(50);
        ImGui.combo("", temp, values);
        return temp.get();
    }

    @Override
    public boolean isFrame() {
        return true;
    }
}
