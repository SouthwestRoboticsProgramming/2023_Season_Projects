package com.team2129.lib.net;

public class NTEnum<T extends Enum<T>> extends NTEntry<T> {
    private static final String METADATA_TABLE = "ShuffleLog_Meta/";

    private final T defaultVal;
    private final Class<T> type;

    public NTEnum(String path, Class<T> type, T defaultVal) {
        super(path, defaultVal);
        this.type = type;
        this.defaultVal = defaultVal;

        NTStringArray metadata = new NTStringArray(METADATA_TABLE + path);
        T[] values = type.getEnumConstants();
        String[] data = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            data[i] = values[i].toString();
        }
        metadata.set(data);
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
