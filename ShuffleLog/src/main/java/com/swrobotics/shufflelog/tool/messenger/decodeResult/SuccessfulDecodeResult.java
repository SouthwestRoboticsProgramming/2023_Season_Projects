package com.swrobotics.shufflelog.tool.messenger.decodeResult;

import com.swrobotics.shufflelog.tool.messenger.data.MessageDataInstance;

public final class SuccessfulDecodeResult implements MessageDataDecodeResult {
    private final MessageDataInstance data;

    public SuccessfulDecodeResult(MessageDataInstance data) {
        this.data = data;
    }

    public MessageDataInstance getData() {
        return data;
    }
}
