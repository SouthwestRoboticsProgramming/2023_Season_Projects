package com.swrobotics.bert.shuffle;

import edu.wpi.first.networktables.NetworkTableEntry;

public abstract class ShuffleBoardTunable<T> extends Tunable<T> {
    protected final NetworkTableEntry entry;

    public ShuffleBoardTunable(NetworkTableEntry entry) {
        this.entry = entry;
    }
}
