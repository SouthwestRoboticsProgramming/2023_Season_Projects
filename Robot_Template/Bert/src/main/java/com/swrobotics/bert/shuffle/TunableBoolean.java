package com.swrobotics.bert.shuffle;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public final class TunableBoolean extends ShuffleBoardTunable<Boolean> {
    private final boolean defaultValue;

    public TunableBoolean(ShuffleboardLayout layout, String name, boolean defaultValue) {
        super(layout.addPersistent(name, defaultValue).withWidget(BuiltInWidgets.kToggleSwitch).getEntry());
        this.defaultValue = defaultValue;

        entry.addListener((event) -> {
            fireOnChanged();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    @Override
    public void set(Boolean value) {
        entry.setBoolean(value);
    }

    @Override
    public Boolean get() {
        return entry.getBoolean(defaultValue);
    }
}
