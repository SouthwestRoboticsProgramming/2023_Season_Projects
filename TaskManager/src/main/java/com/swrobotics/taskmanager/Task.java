package com.swrobotics.taskmanager;

import java.io.File;

public final class Task {
    // Settings
    private File workingDirectory;
    private String[] command;
    private boolean enabled;

    // Status
    private transient boolean running;
    private transient int startupAttemptCount;

    public Task(File workingDirectory, String[] command, boolean enabled) {
        this.workingDirectory = workingDirectory;
        this.command = command;
        this.enabled = enabled;

        running = false;
        startupAttemptCount = 0;
    }
}
