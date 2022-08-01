package com.swrobotics.taskmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class TaskManagerConfiguration {
    private static final String KEY_MESSENGER_HOST = "messengerHost";
    private static final String KEY_MESSENGER_PORT = "messengerPort";
    private static final String KEY_MESSENGER_NAME = "messengerName";
    private static final String KEY_TASKS_ROOT = "tasksRoot";

    private static final Properties DEFAULTS = new Properties();
    static {
        DEFAULTS.setProperty(KEY_MESSENGER_HOST, "localhost");
        DEFAULTS.setProperty(KEY_MESSENGER_PORT, "5805");
        DEFAULTS.setProperty(KEY_MESSENGER_NAME, "TaskManager");
        DEFAULTS.setProperty(KEY_TASKS_ROOT, "tasks");
    }

    private final String messengerHost;
    private final int messengerPort;
    private final String messengerName;
    private final File tasksRoot;

    public TaskManagerConfiguration(File file) {
        Properties props = new Properties(DEFAULTS);
        try {
            props.load(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("Configuration file not found, saving defaults");
            try {
                DEFAULTS.store(new FileWriter(file), "TaskManager configuration");
            } catch (IOException e2) {
                throw new RuntimeException("Failed to save default configuration", e2);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }

        messengerHost = props.getProperty(KEY_MESSENGER_HOST);
        messengerPort = Integer.parseInt(props.getProperty(KEY_MESSENGER_PORT));
        messengerName = props.getProperty(KEY_MESSENGER_NAME);
        tasksRoot     = new File(props.getProperty(KEY_TASKS_ROOT));
    }

    public String getMessengerHost() {
        return messengerHost;
    }

    public int getMessengerPort() {
        return messengerPort;
    }

    public String getMessengerName() {
        return messengerName;
    }

    public File getTasksRoot() {
        return tasksRoot;
    }
}
