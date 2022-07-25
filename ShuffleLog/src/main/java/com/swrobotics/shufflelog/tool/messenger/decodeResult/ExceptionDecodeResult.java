package com.swrobotics.shufflelog.tool.messenger.decodeResult;

public final class ExceptionDecodeResult implements MessageDataDecodeResult {
    private final Throwable exception;

    public ExceptionDecodeResult(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
