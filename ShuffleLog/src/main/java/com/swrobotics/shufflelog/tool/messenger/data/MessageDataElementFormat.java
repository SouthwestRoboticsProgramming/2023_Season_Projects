package com.swrobotics.shufflelog.tool.messenger.data;

import com.swrobotics.messenger.client.MessageReader;

public final class MessageDataElementFormat {
    private String name;
    private MessageDataElementType type;

    public MessageDataElementFormat(String name, MessageDataElementType type) {
        this.name = name;
        this.type = type;
    }

    public MessageDataElementFormat copy() {
        return new MessageDataElementFormat(name, type);
    }

    public MessageDataElementInstance<?> read(MessageReader reader) {
        return type.read(this, reader);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessageDataElementType getType() {
        return type;
    }

    public void setType(MessageDataElementType type) {
        this.type = type;
    }
}
