package com.swrobotics.bert.shuffle;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public final class TunableDouble extends ShuffleBoardTunable<Double> {
    private final double defaultValue;

    public TunableDouble(ShuffleboardLayout layout, String name, double defaultValue) {
        super(layout.addPersistent(name, defaultValue).getEntry());
        this.defaultValue = defaultValue;

        entry.addListener((event) -> {
            fireOnChanged();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    @Override
    public void set(Double value) {
        entry.setDouble(value);
    }

    @Override
    public Double get() {
        return entry.getDouble(defaultValue);
    }
}
