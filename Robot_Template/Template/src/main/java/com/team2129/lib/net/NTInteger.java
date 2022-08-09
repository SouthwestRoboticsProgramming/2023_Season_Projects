package com.team2129.lib.net;

public final class NTInteger extends NTEntry<Integer> {
    private final int defaultVal;

    /**
     * Creates a new {@code int} entry with a specified path.
     * The path can be split using the '/' character to organize
     * entries into groups.
     *
     * @param path path
     */
    public NTInteger(String path, int defaultVal) {
        super(path);
        this.defaultVal = defaultVal;

        // Ensure entry actually exists so it is editable
        if (!entry.exists()) {
            entry.setDouble(defaultVal);
        }
    }

    @Override
    public Integer get() {
        return entry.getNumber(defaultVal).intValue();
    }

    @Override
    public void set(Integer value) {
        entry.setNumber(value);
    }
}
