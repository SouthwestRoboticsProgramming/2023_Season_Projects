package com.team2129.lib.net;

public class NTBoolean extends NTEntry<Boolean> {
    private final boolean defaultVal;

    public NTBoolean(String path, boolean defaultVal) {
        super(path, defaultVal);
        this.defaultVal = defaultVal;
    }

    @Override
    public Boolean get() {
        return entry.getBoolean(defaultVal);
    }

    @Override
    public void set(Boolean value) {
        entry.setBoolean(value);
        
    }
    
}
