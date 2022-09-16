package com.team2129.lib.net;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Represents one data entry in NetworkTables.
 *
 * @param <T> data type
 */
public abstract class NTEntry<T> implements Supplier<T> {
    private final Set<Runnable> changeListeners;
    protected final NetworkTableEntry entry;

    /**
     * Creates a new entry with a specified path.
     * The path can be split using the '/' character to organize
     * entries into groups.
     *
     * @param path path
     */
    public NTEntry(String path, T defaultVal) {
        // Weak set so we don't hold onto expired references
        changeListeners = Collections.newSetFromMap(new WeakHashMap<>());

        NetworkTable table = NetworkTableInstance.getDefault().getTable("");
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            table = table.getSubTable(parts[i]);
        }
        entry = table.getEntry(parts[parts.length - 1]);

        // Ensure entry actually exists so it is editable
        if (!entry.exists())
            set(defaultVal);

        entry.setPersistent();
        entry.addListener((event) -> {
            fireOnChanged();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    public abstract void set(T value);

    public NTEntry<T> setTemporary() {
        entry.clearPersistent();
        return this;
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
