package com.swrobotics.robot.blockauto.part;

import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;

public final class EnumPart<E extends Enum<E>> implements ParamPart {
    private final E[] values;
    private final int defIdx;

    public EnumPart(Class<E> type, E def) {
        values = type.getEnumConstants();
        int defIdx = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(def)) {
                defIdx = i;
            }
        }
        this.defIdx = defIdx;
    }

    @Override
    public Object readInst(MessageReader reader) {
        int i = reader.readInt();
        return values[i];
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(PartTypes.ENUM.getId());
        builder.addInt(values.length);
        for (E value : values) {
            builder.addString(value.name());
        }
        builder.addInt(defIdx);
    }
}
