package com.team2129.lib.net;

public final class NTString extends NTEntry<String> {
    private final String defaultVal;

    public NTString(String path, String defaultVal) {
        super(path, defaultVal);
        this.defaultVal = defaultVal;
    }

    @Override
    public String get() {
        return entry.getString(defaultVal);
    }
}
