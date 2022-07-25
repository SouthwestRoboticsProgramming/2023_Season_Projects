package com.swrobotics.shufflelog.tool.messenger.data;

import com.swrobotics.messenger.client.MessageReader;

import java.util.ArrayList;
import java.util.List;

public final class MessageDataFormat {
    private final List<MessageDataElementFormat> elementFormats;
    private boolean readOnly;

    public MessageDataFormat() {
        elementFormats = new ArrayList<>();
        readOnly = false;
    }

    public MessageDataFormat copy() {
        MessageDataFormat copy = new MessageDataFormat();
        copy.readOnly = readOnly;
        for (MessageDataElementFormat element : elementFormats) {
            copy.elementFormats.add(element.copy());
        }
        return copy;
    }

    public void addElement(MessageDataElementFormat element) {
        elementFormats.add(element);
    }

    public void removeElement(MessageDataElementFormat element) {
        elementFormats.remove(element);
    }

    public List<MessageDataElementFormat> getElementFormats() {
        return elementFormats;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly() {
        readOnly = true;
    }

    public MessageDataInstance decode(MessageReader reader) {
        List<MessageDataElementInstance<?>> instances = new ArrayList<>();
        for (MessageDataElementFormat format : elementFormats) {
            instances.add(format.read(reader));
        }
        return new MessageDataInstance(instances);
    }
}
