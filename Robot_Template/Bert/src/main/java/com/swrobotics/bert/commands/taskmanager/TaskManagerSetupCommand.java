package com.swrobotics.bert.commands.taskmanager;

import com.swrobotics.bert.commands.Command;
import com.swrobotics.taskmanager.api.TaskManagerAPI;

import static com.swrobotics.bert.constants.Constants.PERIODIC_PER_SECOND;

public final class TaskManagerSetupCommand implements Command {
    // you're mom
    private final TaskManagerAPI api;
    private final String[] requiredTasks;

    public TaskManagerSetupCommand(TaskManagerAPI api, String... requiredTasks) {
        this.api = api;
        this.requiredTasks = requiredTasks;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean run() {
        api.ping();
        if (!api.isAlive()) {
            System.out.println("Task manager not ready");
            return false;
        }

        for (String task : requiredTasks) {
            api.taskExists(task).thenAccept((exists) -> {
                if (!exists) {
                    System.out.println("Warning: Task '" + task + "' required but not found");
                }
            });
        }

        for (String task : requiredTasks) {
            api.startTask(task);
            System.out.println("Requested to start task '" + task + "'");
        }

        System.out.println("Task manager is ready");

        return true;
    }

    @Override
    public void end() {

    }

    @Override
    public int getInterval() {
        return PERIODIC_PER_SECOND;
    }
}
