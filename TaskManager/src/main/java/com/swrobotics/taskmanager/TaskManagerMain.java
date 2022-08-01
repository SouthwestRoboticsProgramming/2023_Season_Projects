package com.swrobotics.taskmanager;

import java.io.File;

public final class TaskManagerMain {
    private static final File CONFIG_FILE = new File("config.properties");

    public static void main(String[] args) {
        // Set up
        TaskManagerConfiguration config = new TaskManagerConfiguration(CONFIG_FILE);
        TaskManagerAPI api = new TaskManagerAPI(config);

        // Read messages for the rest of eternity
        while (true) {
            api.read();

            try {
                Thread.sleep(1000 / 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
