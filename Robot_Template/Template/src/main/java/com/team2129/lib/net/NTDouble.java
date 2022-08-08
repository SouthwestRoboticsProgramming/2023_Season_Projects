package com.team2129.lib.net;

public final class NTDouble extends NTEntry<Double> {
    private final double defaultVal;

    /**
     * Creates a new {@code double} entry with a specified path.
     * The path can be split using the '/' character to organize
     * entries into groups.
     *
     * @param path path
     */
    public NTDouble(String path, double defaultVal) {
        super(path);
        this.defaultVal = defaultVal;
    }

    @Override
    public Double get() {
        return entry.getDouble(defaultVal);
    }

    @Override
    public void set(Double value) {
        entry.setDouble(value);
    }
}
