package com.swrobotics.shufflelog.tool.messenger.data;

public final class MessageDataElementInstance<T> {
    private final MessageDataElementFormat struct;
    private final T value;

    public MessageDataElementInstance(MessageDataElementFormat struct, T value) {
        this.struct = struct;
        this.value = value;
    }

    public String getName() {
        return struct.getName();
    }

    public T getValue() {
        return value;
    }
}
