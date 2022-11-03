package com.swrobotics.shufflelog.tool;

public interface Tool {
    void process();

    default void onKeyPress(char key, int keyCode) {}
    default void onKeyRelease(char key, int keyCode) {}
}
