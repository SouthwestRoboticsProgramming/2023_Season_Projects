package com.swrobotics.taskmanager.api;

@FunctionalInterface
public interface TaskOutputHandler {
    void handle(String task, String line);
}
