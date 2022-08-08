package com.team2129.lib.net;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents one data entry in NetworkTables.
 *
 * @param <T> data type
 */
public abstract class NTEntry<T> {
    private final Set<Runnable> changeListeners;
    protected final NetworkTableEntry entry;

    /**
     * Creates a new entry with a specified path.
     * The path can be split using the '/' character to organize
     * entries into groups.
     *
     * @param path path
     */
    public NTEntry(String path) {
        changeListeners = new HashSet<>();
        entry = NetworkTableInstance.getDefault().getEntry(path);
        entry.setPersistent();
        entry.addListener((event) -> {
            fireOnChanged();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    public abstract T get();
    public abstract void set(T value);

    public void setTemporary() {
        entry.clearPersistent();
    }

    public void onChange(Runnable listener) {
        changeListeners.add(listener);
    }

    private void fireOnChanged() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }
}
