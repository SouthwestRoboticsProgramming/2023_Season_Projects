package com.swrobotics.shufflelog.tool.messenger.data;

import java.util.List;

public final class MessageDataInstance {
    private final List<MessageDataElementInstance<?>> elements;

    public MessageDataInstance(List<MessageDataElementInstance<?>> elements) {
        this.elements = elements;
    }

    public List<MessageDataElementInstance<?>> getElements() {
        return elements;
    }
}
