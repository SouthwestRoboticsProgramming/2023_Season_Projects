package com.swrobotics.robot.subsystem.thrower;

public enum ThrowerTuneSelector {
    DEFAULT("Thrower/Tuning/Default"),
    LAST_MATCH("Thrower/Tuning/Last_Match"),
    TWO_MATCHES_AGO("Thrower/Tuning/Two_Matches_Ago");

    private final String path;

    private ThrowerTuneSelector(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
