package com.swrobotics.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class TaskManager {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(File.class, new FileTypeAdapter())
            .setPrettyPrinting()
            .create();
    private static final Type TASKS_MAP_TYPE = new TypeToken<Map<String, Task>>(){}.getType();

    private static final File CONFIG_FILE = new File("config.json");
    private static final File TASKS_FILE = new File("tasks.json");

    private final TaskManagerAPI api;
    private final Map<String, Task> tasks;

    public TaskManager() {
        TaskManagerConfiguration config = TaskManagerConfiguration.load(CONFIG_FILE);
        api = new TaskManagerAPI(config);
        tasks = loadTasks();

        tasks.put("Test", new Task(config.getTasksRoot(), new String[]{"hello", "world"}, true));
    }

    private Map<String, Task> loadTasks() {
        // If the file doesn't exist, there must not be any tasks yet
        if (!TASKS_FILE.exists())
            return new HashMap<>();

        try {
            return GSON.fromJson(new FileReader(TASKS_FILE), TASKS_MAP_TYPE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tasks file");
        }
    }

    private void saveTasks() {
        try {
            GSON.toJson(tasks, new FileWriter(TASKS_FILE));
        } catch (Exception e) {
            System.err.println("Failed to save tasks file");
            e.printStackTrace();
        }
    }

    public void run() {
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
