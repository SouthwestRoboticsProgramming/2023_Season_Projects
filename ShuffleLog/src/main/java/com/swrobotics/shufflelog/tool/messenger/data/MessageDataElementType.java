package com.swrobotics.shufflelog.tool.messenger.data;

import com.swrobotics.messenger.client.MessageReader;

public enum MessageDataElementType {
    BOOLEAN {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readBoolean());
        }
    },
    STRING {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readString());
        }
    },
    CHAR {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readChar());
        }
    },
    BYTE {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readByte());
        }
    },
    SHORT {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readShort());
        }
    },
    INT {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readInt());
        }
    },
    LONG {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readLong());
        }
    },
    FLOAT {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readFloat());
        }
    },
    DOUBLE {
        @Override
        public MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader) {
            return new MessageDataElementInstance<>(struct, reader.readDouble());
        }
    };

    public abstract MessageDataElementInstance<?> read(MessageDataElementFormat struct, MessageReader reader);
}
