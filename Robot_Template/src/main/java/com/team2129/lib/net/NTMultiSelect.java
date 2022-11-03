package com.team2129.lib.net;

import java.util.Arrays;
import java.util.List;

public abstract class NTMultiSelect<T> extends NTEntry<T> {
    private final T defaultVal;
    private final String defaultName;
    private final NTStringArray metadata;

    public NTMultiSelect(String path, T defaultVal) {
        super(path, defaultVal);
        this.defaultVal = defaultVal;
        defaultName = nameOf(defaultVal);

        metadata = new NTStringArray(ShuffleLog.METADATA_TABLE + path);
        metadata.setTemporary();
    }

    // May throw IllegalArgumentException
    protected abstract T valueOf(String name);

    protected abstract String nameOf(T t);

    @SafeVarargs
    public final void setOptions(T... options) {
        setOptions(Arrays.asList(options));
    }

    public void setOptions(List<T> options) {
        String[] data = new String[options.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = nameOf(options.get(i));
        }
        metadata.set(data);
    }

    @Override
    public T get() {
        try {
            return valueOf(entry.getString(defaultName));
        } catch (IllegalArgumentException e) {
            return defaultVal;
        }
    }

    @Override
    public void set(T value) {
        entry.setString(nameOf(value));
    }
}
