package com.team2129.lib.net;

public class NTDoubleArray extends NTEntry<double[]> {
    private final double[] defaultVals;

    public NTDoubleArray(String path, double... defaultVals) {
        super(path, defaultVals);
        this.defaultVals = defaultVals;
    }

    @Override
    public double[] get() {
        return entry.getDoubleArray(defaultVals);
    }

    public double get(int index) {
        return get()[index];
    }

    @Override
    public void set(double[] value) {
        entry.setDoubleArray(value);
    }

    public void set(int index, double value) {
        double[] data = get();
        data[index] = value;
        set(data);
    }
}
