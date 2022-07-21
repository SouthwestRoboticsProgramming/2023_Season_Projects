package com.swrobotics.bert.shuffle;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public final class TunableDoubleArray extends ShuffleBoardTunable<double[]> {
    private final double[] defaultValues;

    public TunableDoubleArray(ShuffleboardLayout layout, String name, double... defaultValues) {
        super(layout.addPersistent(name, defaultValues).getEntry());
        this.defaultValues = defaultValues;

        entry.addListener((event) -> {
            fireOnChanged();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    @Override
    public void set(double[] values) {
        entry.setDoubleArray(values);
    }

    @Override
    public double[] get() {
        return entry.getDoubleArray(defaultValues);
    }
}
