package com.team2129.lib.net;

public class NTEnum<T extends Enum<T>> extends NTMultiSelect<T> {
    private final Class<T> type;

    public NTEnum(String path, Class<T> type, T defaultVal) {
        super(path, defaultVal);
        this.type = type;

        setOptions(type.getEnumConstants());
    }

    @Override
    protected T valueOf(String name) {
        return Enum.valueOf(type, name);
    }

    @Override
    protected String nameOf(T t) {
        return t.name();
    }
}
