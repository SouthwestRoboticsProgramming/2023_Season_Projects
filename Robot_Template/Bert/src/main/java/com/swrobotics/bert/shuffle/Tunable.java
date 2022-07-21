package com.swrobotics.bert.shuffle;

import java.util.HashSet;
import java.util.Set;

public abstract class Tunable<T> {
    private final Set<Runnable> changeListeners;

    public Tunable() {
        changeListeners = new HashSet<>();
    }

    public abstract void set(T value);
    public abstract T get();

    public void onChange(Runnable listener) {
        changeListeners.add(listener);
    }

    protected void fireOnChanged() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }
}
