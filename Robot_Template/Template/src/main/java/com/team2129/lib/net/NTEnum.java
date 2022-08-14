package com.team2129.lib.net;

public class NTEnum<T extends Enum<T>> extends NTEntry<T> {
    private final T defaultVal;
    private final Class<T> type;

    public NTEnum(String path, Class<T> type, T defaultVal) {
        super(path, defaultVal);
        this.type = type;
        this.defaultVal = defaultVal;
    }

    @Override
    public T get() {
        try {
            return Enum.valueOf(type, entry.getString(defaultVal.toString()));
        } catch (IllegalArgumentException e) {
            return defaultVal;
        }
    }

    @Override
    public void set(T value) {
        entry.setString(value.toString());
        
    }
    
}
