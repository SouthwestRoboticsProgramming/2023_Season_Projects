package com.swrobotics.bert.shuffle;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public final class TunableInt extends ShuffleBoardTunable<Integer> {
    private final int defaultValue;

    public TunableInt(ShuffleboardLayout layout, String name, int defaultValue) {
        super(layout.addPersistent(name, defaultValue).getEntry());
        this.defaultValue = defaultValue;

        entry.addListener((event) -> {
            fireOnChanged();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    @Override
    public void set(Integer value) {
        entry.setDouble(value);
    }

    @Override
    public Integer get() {
        return entry.getNumber(defaultValue).intValue();
    }
}
