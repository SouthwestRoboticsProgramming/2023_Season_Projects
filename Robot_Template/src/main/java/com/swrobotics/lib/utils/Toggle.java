package com.swrobotics.lib.utils;

public class Toggle {
    private boolean bool;

    public void toggle() {
        bool = !bool;
    }

    public void toggle(boolean toggle) {
        if (toggle) toggle();
    }

    public void set(boolean bool) {
        this.bool = bool;
    }

    public boolean get() {
        return bool;
    }
}
